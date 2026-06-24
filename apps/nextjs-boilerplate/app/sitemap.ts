import type { MetadataRoute } from "next";
import { siteConfig } from "@/lib/site-config";

// Generates /sitemap.xml. Add an entry per indexable route. For large or
// dynamic route sets, map over your data source here (or split with
// `generateSitemaps`).
export default function sitemap(): MetadataRoute.Sitemap {
	return [
		{
			url: siteConfig.url,
			lastModified: new Date(),
			changeFrequency: "monthly",
			priority: 1,
		},
	];
}
