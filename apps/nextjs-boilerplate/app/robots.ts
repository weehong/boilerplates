import type { MetadataRoute } from "next";
import { isProductionEnv, siteConfig } from "@/lib/site-config";

// Generates /robots.txt. Only production allows crawling/indexing; every other
// environment blocks all robots so previews/staging are never indexed.
export default function robots(): MetadataRoute.Robots {
	if (!isProductionEnv) {
		return {
			rules: { userAgent: "*", disallow: "/" },
		};
	}

	return {
		rules: { userAgent: "*", allow: "/" },
		sitemap: `${siteConfig.url}/sitemap.xml`,
		host: siteConfig.url,
	};
}
