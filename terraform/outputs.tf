output "storage_account_url" {
  description = "Primary blob service endpoint"
  value       = azurerm_storage_account.storage.primary_blob_endpoint
}

#output "cdn_endpoint_url" {
#  description = "CDN endpoint URL"
#  value       = "https://${azurerm_cdn_endpoint.endpoint.host_name}"
#}
#
#output "resource_group_name" {
#  description = "Resource group name"
#  value       = azurerm_resource_group.rg.name
#}
#
#output "key_vault_uri" {
#  description = "Key Vault URI"
#  value       = azurerm_key_vault.vault.vault_uri
#}