import { ImageResponse } from "next/og";
import { siteConfig } from "@/lib/site-config";

// Generated Apple touch icon (/apple-icon).
export const size = { width: 180, height: 180 };
export const contentType = "image/png";

export default function AppleIcon(): ImageResponse {
	return new ImageResponse(
		<div
			style={{
				width: "100%",
				height: "100%",
				display: "flex",
				alignItems: "center",
				justifyContent: "center",
				background: siteConfig.themeColor.dark,
				color: siteConfig.themeColor.light,
				fontSize: 110,
				fontWeight: 700,
			}}
		>
			{siteConfig.shortName.charAt(0)}
		</div>,
		{ ...size }
	);
}
