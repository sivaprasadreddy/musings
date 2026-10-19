import { defineRailway, github, postgres, project, service } from "railway/iac";

export default defineRailway(() => {
    const db = postgres("Postgres");
    const app = service("musings", {
        source: github("sivaprasadreddy/musings", {branch: "main", checkSuites: false}),
        //source: image("sivaprasadreddy/musings:latest"),
        build: {buildEnvironment: "V3", builder: "DOCKERFILE", dockerfilePath: "/Dockerfile"},
        env: {
            DB_HOST: db.env.PGHOST,
            DB_PORT: db.env.PGPORT,
            DB_NAME: db.env.PGDATABASE,
            DB_USERNAME: db.env.PGUSER,
            DB_PASSWORD: db.env.PGPASSWORD,
            BOOTUI_ENABLED: 'off',
            BOOTUI_MCP_ENABLED: 'off',
        },
        healthcheck: '/actuator/health',
        healthcheckTimeout: 30,
        replicas: {
            "asia-southeast1-eqsg3a": 1,
        },
    });

    return project("musings-prod", { resources: [app, db] });
});
