(function () {
    function applyTheme(theme) {
        if (theme === "light") {
            document.body.classList.add("light-theme");
            $("#themeToggle i")
                .removeClass("fa-moon")
                .addClass("fa-sun");
        } else {
            document.body.classList.remove("light-theme");
            $("#themeToggle i")
                .removeClass("fa-sun")
                .addClass("fa-moon");
        }
    }

    $(document).ready(function () {
        const savedTheme = localStorage.getItem("theme") || "dark";

        applyTheme(savedTheme);

        $(document).on("click", "#themeToggle", function (e) {
            e.preventDefault();

            const currentTheme = localStorage.getItem("theme") || "dark";
            const newTheme = currentTheme === "dark" ? "light" : "dark";

            localStorage.setItem("theme", newTheme);
            applyTheme(newTheme);
        });
    });
})();