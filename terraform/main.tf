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
  name     = "daily-brew-images-rg"
  location = "westeurope"

  tags = {
    Environment = "Production"
    Project     = "Daily Brew"
  }
}

# Storage account
resource "azurerm_storage_account" "storage" {
  name                     = "dailybrewstorage"
  resource_group_name      = azurerm_resource_group.rg.name
  location                = azurerm_resource_group.rg.location
  account_tier            = "Standard"
  account_replication_type = "LRS"
  account_kind            = "StorageV2"
  enable_https_traffic_only = true
  min_tls_version         = "TLS1_2"

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
  name                  = "dailybrewcontainer"
  storage_account_name  = azurerm_storage_account.storage.name
  container_access_type = "blob"
}

## CDN Profile
#resource "azurerm_cdn_profile" "cdn" {
#  name                = "daily-brew-cdn"
#  location            = azurerm_resource_group.rg.location
#  resource_group_name = azurerm_resource_group.rg.name
#  sku                 = "Standard_Microsoft"
#}
#
## CDN Endpoint
#resource "azurerm_cdn_endpoint" "endpoint" {
#  name                = "daily-brew-images"
#  profile_name        = azurerm_cdn_profile.cdn.name
#  location            = azurerm_resource_group.rg.location
#  resource_group_name = azurerm_resource_group.rg.name
#
#  origin {
#    name       = "blobstorage"
#    host_name  = azurerm_storage_account.storage.primary_blob_host
#  }
#
#  optimization_type = "GeneralWebDelivery"
#
#  delivery_rule {
#    name  = "EnforceHTTPS"
#    order = 1
#
#    request_scheme_condition {
#      operator     = "Equal"
#      match_values = ["HTTP"]
#    }
#
#    url_redirect_action {
#      redirect_type = "Found"
#      protocol      = "Https"
#    }
#  }
#
#  delivery_rule {
#    name  = "CacheExpiration"
#    order = 2
#
#    request_scheme_condition {
#      operator     = "Equal"
#      match_values = ["HTTPS"]
#    }
#
#    cache_expiration_action {
#      behavior = "Override"
#      duration = "7.00:00:00"
#    }
#  }
#
#  # Image optimization rules
#  delivery_rule {
#    name  = "ImageOptimization"
#    order = 3
#
#    file_extension_condition {
#      operator     = "Equal"
#      match_values = ["jpg", "jpeg", "png", "gif"]
#    }
#
#    cache_expiration_action {
#      behavior = "Override"
#      duration = "7.00:00:00"
#    }
#
#    cache_key_query_string_action {
#      behavior = "Include"
#      parameters = ["width", "height", "quality"]
#    }
#  }
#}
#
## Key Vault for storing secrets
#resource "azurerm_key_vault" "vault" {
#  name                        = "daily-brew-vault"
#  location                    = azurerm_resource_group.rg.location
#  resource_group_name         = azurerm_resource_group.rg.name
#  enabled_for_disk_encryption = true
#  tenant_id                   = data.azurerm_client_config.current.tenant_id
#  soft_delete_retention_days  = 7
#  purge_protection_enabled    = false
#  sku_name                   = "standard"
#
#  access_policy {
#    tenant_id = data.azurerm_client_config.current.tenant_id
#    object_id = data.azurerm_client_config.current.object_id
#
#    key_permissions = [
#      "Get", "List", "Create", "Delete"
#    ]
#
#    secret_permissions = [
#      "Get", "List", "Set", "Delete"
#    ]
#  }
#}
#
## Store storage account key in Key Vault
#resource "azurerm_key_vault_secret" "storage_key" {
#  name         = "storage-account-key"
#  value        = azurerm_storage_account.storage.primary_access_key
#  key_vault_id = azurerm_key_vault.vault.id
#}
#
## Outputs
#output "cdn_endpoint_hostname" {
#  value = azurerm_cdn_endpoint.endpoint.host_name
#}

output "storage_account_name" {
  value = azurerm_storage_account.storage.name
}

#output "key_vault_name" {
#  value = azurerm_key_vault.vault.name
#}

# Data source for current Azure configuration
data "azurerm_client_config" "current" {}