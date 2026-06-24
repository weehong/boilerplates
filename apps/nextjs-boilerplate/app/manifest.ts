import type { MetadataRoute } from "next";
import { siteConfig } from "@/lib/site-config";

// Generates /manifest.webmanifest. Icons point at the generated /icon and
// /apple-icon routes. For full PWA installability add static 192x192 and
// 512x512 (maskable) PNGs and reference them here too.
export default function manifest(): MetadataRoute.Manifest {
	return {
		name: siteConfig.name,
		short_name: siteConfig.shortName,
		description: siteConfig.description,
		start_url: "/",
		display: "standalone",
		background_color: siteConfig.themeColor.light,
		theme_color: siteConfig.themeColor.light,
		icons: [
			{ src: "/icon", sizes: "32x32", type: "image/png" },
			{ src: "/apple-icon", sizes: "180x180", type: "image/png" },
		],
	};
}
