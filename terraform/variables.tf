variable "resource_group" {
  description = "Name of the resource group"
  type        = string
  default     = "daily-brew-images-rg"
}

variable "storage_account" {
  description = "Name of the storage"
  type        = string
  default     = "dailybrewstorage"
}

variable "storage_container" {
  description = "Name of the storage container"
  type        = string
  default     = "dailybrewcontainer"
}

variable "postgres_admin" {
  default = "pgadmin"
}

variable "postgres_password" {
  default = "SuperSecurePassword123!"
}

variable "app_service_name" {
  default = "my-app-service"
}

variable "postgres_server_name" {
  default = "mypostgresdb"
}