import type { Organization, WebSite, WithContext } from "schema-dts";
import { siteConfig } from "@/lib/site-config";

/** Site-wide WebSite schema (enables sitelinks search box eligibility). */
export function getWebSiteSchema(): WithContext<WebSite> {
	return {
		"@context": "https://schema.org",
		"@type": "WebSite",
		name: siteConfig.name,
		description: siteConfig.description,
		url: siteConfig.url,
	};
}

/** Site-wide Organization schema (publisher identity). */
export function getOrganizationSchema(): WithContext<Organization> {
	return {
		"@context": "https://schema.org",
		"@type": "Organization",
		name: siteConfig.name,
		url: siteConfig.url,
		logo: `${siteConfig.url}/icon`,
	};
}
