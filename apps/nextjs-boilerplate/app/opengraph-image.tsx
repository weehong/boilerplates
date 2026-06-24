import { ImageResponse } from "next/og";
import { siteConfig } from "@/lib/site-config";

// Generated social-share image (/opengraph-image), 1200x630.
// next/og supports flexbox + a subset of CSS only.
export const alt = siteConfig.ogImageAlt;
export const size = { width: 1200, height: 630 };
export const contentType = "image/png";

export default function OpengraphImage(): ImageResponse {
	return new ImageResponse(
		<div
			style={{
				width: "100%",
				height: "100%",
				display: "flex",
				flexDirection: "column",
				justifyContent: "center",
				padding: "80px",
				background: siteConfig.themeColor.dark,
				color: siteConfig.themeColor.light,
			}}
		>
			<div style={{ fontSize: 72, fontWeight: 700, lineHeight: 1.1 }}>
				{siteConfig.name}
			</div>
			<div
				style={{
					marginTop: 24,
					fontSize: 32,
					opacity: 0.8,
					maxWidth: 900,
				}}
			>
				{siteConfig.description}
			</div>
		</div>,
		{ ...size }
	);
}
