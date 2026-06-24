import { defineConfig, globalIgnores } from "eslint/config";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";
import eslintConfigPrettier from "eslint-config-prettier";
import eslintPluginUnicorn from "eslint-plugin-unicorn";
import tseslint from "typescript-eslint";

// Application source globs the strict, type-aware rules apply to.
// (Config/test/storybook files keep the lighter eslint-config-next baseline.)
const APP_GLOBS = [
  "app/**/*.{ts,tsx}",
  "components/**/*.{ts,tsx}",
  "lib/**/*.{ts,tsx}",
];

const eslintConfig = defineConfig([
  // Next.js baseline (covers React, react-hooks, jsx-a11y, import).
  ...nextVitals,
  ...nextTs,

  // Core ESLint correctness rules (ported from the shared boilerplate).
  {
    name: "eslint-base",
    rules: {
      "no-await-in-loop": "error",
      "no-constant-binary-expression": "error",
      "no-duplicate-imports": "error",
      "no-new-native-nonconstructor": "error",
      "no-promise-executor-return": "error",
      "no-self-compare": "error",
      "no-template-curly-in-string": "error",
      "no-unmodified-loop-condition": "error",
      "no-unreachable-loop": "error",
      "no-unused-private-class-members": "error",
      "no-use-before-define": "error",
      "require-atomic-updates": "error",
      // Property names often mirror external schemas (e.g. web app manifest
      // `short_name`, OG `og:*`), so only enforce camelCase on bindings.
      camelcase: ["error", { ignoreImports: true, properties: "never" }],
    },
  },

  // Strict, type-aware TypeScript layer — application code only.
  {
    name: "strict-typescript",
    files: APP_GLOBS,
    extends: [tseslint.configs.recommendedTypeChecked],
    languageOptions: {
      parserOptions: {
        projectService: true,
        tsconfigRootDir: import.meta.dirname,
      },
    },
    rules: {
      "@typescript-eslint/adjacent-overload-signatures": "error",
      "@typescript-eslint/array-type": ["error", { default: "generic" }],
      "@typescript-eslint/consistent-type-exports": "error",
      "@typescript-eslint/consistent-type-imports": "error",
      "@typescript-eslint/explicit-function-return-type": "error",
      "@typescript-eslint/explicit-member-accessibility": "error",
      "@typescript-eslint/explicit-module-boundary-types": "error",
      "@typescript-eslint/no-confusing-void-expression": "error",
      "@typescript-eslint/no-import-type-side-effects": "error",
      "@typescript-eslint/no-require-imports": "error",
      "@typescript-eslint/no-unused-vars": "error",
      "@typescript-eslint/no-useless-empty-export": "error",
      "@typescript-eslint/prefer-enum-initializers": "error",
      "@typescript-eslint/prefer-readonly": "error",
      "no-return-await": "off",
      "@typescript-eslint/return-await": "error",
      "@typescript-eslint/no-misused-promises": [
        "error",
        { checksVoidReturn: { attributes: false } },
      ],
    },
  },

  // Unicorn rules — application code only.
  {
    name: "unicorn",
    files: APP_GLOBS,
    plugins: { unicorn: eslintPluginUnicorn },
    rules: {
      "unicorn/custom-error-definition": "error",
      "unicorn/empty-brace-spaces": "error",
      "unicorn/no-array-for-each": "off",
      "unicorn/no-array-reduce": "off",
      "unicorn/no-console-spaces": "error",
      "unicorn/no-null": "off",
      "unicorn/filename-case": "off",
      "unicorn/prevent-abbreviations": [
        "error",
        {
          replacements: {
            db: false,
            arg: false,
            args: false,
            env: false,
            fn: false,
            func: { fn: true, function: false },
            prop: false,
            props: false,
            ref: false,
            refs: false,
            util: false,
            utils: false,
          },
          ignore: ["semVer", "SemVer"],
        },
      ],
    },
  },

  // Disable formatting rules that conflict with Prettier (must stay last).
  eslintConfigPrettier,

  // Override default ignores of eslint-config-next.
  globalIgnores([".next/**", "out/**", "build/**", "next-env.d.ts"]),
]);

export default eslintConfig;
