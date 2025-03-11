terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=3.0.0"
    }
  }
}

provider "azurerm" {
  features {}
}

# Resource group
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group
  location = "westeurope"

  tags = {
    Environment = "Production"
    Project     = "Daily Brew"
  }
}

# Storage account
resource "azurerm_storage_account" "storage" {
  name                      = var.storage_account
  resource_group_name       = azurerm_resource_group.rg.name
  location                  = "westeurope"
  account_tier              = "Standard"
  account_replication_type  = "LRS"
  account_kind              = "StorageV2"
  enable_https_traffic_only = true
  min_tls_version           = "TLS1_2"

  static_website {
    index_document = "index.html"
  }

  blob_properties {
    cors_rule {
      allowed_headers    = ["*"]
      allowed_methods    = ["GET", "HEAD"]
      allowed_origins    = ["*"] # Restrict this in production
      exposed_headers    = ["*"]
      max_age_in_seconds = 3600
    }
  }

  network_rules {
    default_action = "Allow"
    bypass         = ["AzureServices"]
  }
}

# Blob container
resource "azurerm_storage_container" "images" {
  name                  = var.storage_container
  storage_account_name  = azurerm_storage_account.storage.name
  container_access_type = "blob"
}

resource "azurerm_virtual_network" "dailybrew-vn" {
  name                = "dailybrew-vn"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  address_space       = ["10.0.0.0/16"]
}

resource "azurerm_subnet" "dailybrew-sn" {
  name                 = "dailybrew-sn"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.dailybrew-vn.name
  address_prefixes     = ["10.0.2.0/24"]
  service_endpoints    = ["Microsoft.Storage"]
  delegation {
    name = "fs"
    service_delegation {
      name    = "Microsoft.DBforPostgreSQL/flexibleServers"
      actions = [
        "Microsoft.Network/virtualNetworks/subnets/join/action",
      ]
    }
  }
}
resource "azurerm_private_dns_zone" "dailybrewdns" {
  name                = "dailybrew.postgres.database.azure.com"
  resource_group_name = azurerm_resource_group.rg.name
}

resource "azurerm_private_dns_zone_virtual_network_link" "example" {
  name                  = "dailybrewVnetZone.com"
  private_dns_zone_name = azurerm_private_dns_zone.dailybrewdns.name
  virtual_network_id    = azurerm_virtual_network.dailybrew-vn.id
  resource_group_name   = azurerm_resource_group.rg.name
  depends_on            = [azurerm_subnet.dailybrew-sn]
}

resource "azurerm_postgresql_flexible_server" "dailybrew-psqlflexibleserver" {
  name                   = "dailybrew-psqlflexibleserver"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = "12"
  delegated_subnet_id    = azurerm_subnet.dailybrew-sn.id
  private_dns_zone_id    = azurerm_private_dns_zone.dailybrewdns.id
  administrator_login    = "psqladmin"
  administrator_password = "H@Sh1CoR3!"
  zone                   = "1"

  storage_mb = 32768

  sku_name   = "B_Standard_B1ms"
  depends_on = [azurerm_private_dns_zone_virtual_network_link.example]
}

resource "azurerm_container_registry" "acr" {
  name                = "acrdailybrew"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Premium"
  admin_enabled       = false
}

output "storage_account_name" {
  value = azurerm_storage_account.storage.name
}


# Data source for current Azure configuration
data "azurerm_client_config" "current" {}