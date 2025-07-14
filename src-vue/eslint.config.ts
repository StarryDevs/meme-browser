import {globalIgnores} from "eslint/config";
import {
    configureVueProject,
    defineConfigWithVueTs,
    vueTsConfigs
} from "@vue/eslint-config-typescript";
import pluginVue from "eslint-plugin-vue";

configureVueProject({scriptLangs: ["ts", "tsx", "js", "jsx"]});

export default defineConfigWithVueTs(
    {
        name: "app/files-to-lint",
        files: ["**/*.{ts,mts,tsx,vue}"],
        rules: {
            "eqeqeq": "off",
            "eol-last": ["error", "always"],
            "comma-spacing": ["error", {"before": false, "after": true}],
            "semi": ["error", "always"],
            "vue/multi-word-component-names": "off",
            "@typescript-eslint/no-unused-vars": "off",
            "@typescript-eslint/no-explicit-any": "off",
            "quotes": ["error", "double"],
            "indent": ["error", 4]
        }
    },

    globalIgnores(["**/dist/**", "**/dist-ssr/**", "**/coverage/**"]),

    pluginVue.configs["flat/essential"],
    vueTsConfigs.recommended,
);
