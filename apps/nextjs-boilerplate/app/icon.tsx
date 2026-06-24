import { ImageResponse } from "next/og";
import { siteConfig } from "@/lib/site-config";

// Generated favicon (/icon). Coexists with app/favicon.ico.
export const size = { width: 32, height: 32 };
export const contentType = "image/png";

export default function Icon(): ImageResponse {
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
				fontSize: 22,
				fontWeight: 700,
				borderRadius: 6,
			}}
		>
			{siteConfig.shortName.charAt(0)}
		</div>,
		{ ...size }
	);
}
