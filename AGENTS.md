# AGENTS.md

This file provides guidance to AI Agents when working with code in this repository.

## Build Commands
- **Full build with tests**: `./mvnw clean verify`
- **Single test class**: `./mvnw test -Dtest=com.example.MyTestClass`
- **Run application**: `./mvnw spring-boot:run`
- **Code formatting**: `./mvnw spotless:apply`
- **Docker image build**: `./mvnw clean compile spring-boot:build-image -DskipTests`

## Commit and Pull Request Guidelines

### Commit Messages
- **Format**: `type(scope): description`
- **Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- **Examples**:
  - `feat(auth): add JWT token refresh functionality`
  - `fix(api): handle null pointer in message service`
  - `test(ui): add component tests for login form`

### Pull Requests
- **Branch naming**: `feat/description`, `fix/issue-number-description`
- **Title**: Clear, concise description of changes
- **Description**: Include what was changed and why
- **Testing**: Ensure all tests pass, coverage meets requirements
- **Code review**: Address all reviewer feedback

## Shadleaf (Thymeleaf UI components)

- The UI uses Shadleaf: UI components as Thymeleaf tags with the `sl:` prefix
  (`xmlns:sl="https://shadleaf.dev/sl"`), rendered on the server. Not React: no JSX, no Radix, no `asChild`.
- Docs as Markdown: https://shadleaf.dev/<version>/llms.txt. Read the component's page
  (`.../components/<name>.md`) before using a component; it ends with the attribute table.
- An attribute that is not a prop passes through to the HTML silently, so a misspelt prop does nothing.
  Use only the props in the attribute table; `th:<prop>="${...}"` sets one from an expression.
- Change a component's look with its props (`variant`, `size`, `border`, ...), not with utility classes:
  the library's stylesheet defines none. `class` is appended to the component's own class.
- A link that looks like a button is `<sl:button as="a" th:href="@{/x}">`. `<sl:button>` renders
  `type="button"`: write `type="submit"` on the one that submits a form.
- Forms: `th:object` on the `<form>`, `th:field` on `<sl:field>` (its label, control and errors share it),
  `<sl:form-errors/>` before the fields. To validate as the user types (htmx 4 only), use
  `<sl:form th:validate="@{/x/validate}">` and an endpoint that renders the same form; no rules in JavaScript.
- Dialogs and sheets need an `id` and open from any button with `commandfor="<id>" command="show-modal"`
  (`command="close"` closes). There are no trigger or close parts. Menus and popovers open with
  `<sl:dropdown-menu-trigger>` / `<sl:popover-trigger>` or `popovertarget`.
- Icons: `<sl:icon name="...">` with Lucide names. An icon-only button (`size="icon"`, `icon-sm`, ...)
  needs an `aria-label`; rendering fails without one.
- Charts: `<sl:chart th:data="${rows}" label="...">` with parts (`sl:chart-x-axis key="month"`,
  `sl:chart-bar key="desktop" th:label="#{...}"`). No `ChartConfig` and no formatter functions: labels and
  colours go on the series parts, number and date formats are names (`format="month-short"`).
- Rendering fails with a message on a wrong prop value, a missing required prop or a missing accessible
  name: render every page you change (run the tests or load the page) and fix what it reports.
- With `hx-boost` on `<body>`, every boosted page load sends `HX-Request`: return a fragment only when the
  request targets one (see the "Application shell" guide, "With htmx").
- CSS: `standalone` (the default). The application does not compile Tailwind, so utility classes do nothing; style the application's own markup in its own stylesheet.
- Alpine: `bundled` (the default). Shadleaf loads and starts Alpine.js; never add another Alpine script. Register the application's own `Alpine.data` on `alpine:init`, in a script before the `sl/layout :: assets` fragment.

For Shadleaf docs refer https://shadleaf.dev/current/llms.txt