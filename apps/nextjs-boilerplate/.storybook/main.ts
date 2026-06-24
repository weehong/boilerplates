import type { StorybookConfig } from "@storybook/nextjs";

const config: StorybookConfig = {
	stories: [
		"../{app,components}/**/*.mdx",
		"../{app,components}/**/*.stories.@(js|jsx|ts|tsx)",
	],
	addons: [
		"@storybook/addon-links",
		"@storybook/addon-themes",
		"@storybook/addon-docs",
	],
	framework: {
		name: "@storybook/nextjs",
		options: {},
	},
	docs: {
		defaultName: "Documentation",
	},
};

export default config;
