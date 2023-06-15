package de.avtest.testaufgabe.juniortask.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/copyright")
public class CopyrightController {

    String gameName = "" +
        " _____ _        _____             _____           \n" +
        "/__   (_) ___  /__   \\__ _  ___  /__   \\___   ___ \n" +
        "  / /\\/ |/ __|   / /\\/ _` |/ __|   / /\\/ _ \\ / _ \\\n" +
        " / /  | | (__   / / | (_| | (__   / / | (_) |  __/\n" +
        " \\/   |_|\\___|  \\/   \\__,_|\\___|  \\/   \\___/ \\___|\n" +
        "                                                  \n";
    String avTest = "" +
        " _               ____        _                 \n" +
        "| |             / __ \\      | |                \n" +
        "| |__   _   _  | |  | | ___ | | __  __ _  _ __ \n" +
        "| '_ \\ | | | | | |  | |/ __|| |/ / / _` || '__|\n" +
        "| |_) || |_| | | |__| |\\__ \\|   < | (_| || |   \n" +
        "|_.__/  \\__, |  \\____/ |___/|_|\\_\\ \\__,_||_|   \n" +
        "         __/ |                                 \n" +
        "        |___/                                  \n" +
        "     _____  _                                      _                        \n" +
        "    / ____|| |                                    | |                       \n" +
        "   | (___  | |_   __ _  _ __    __ _   ___  _ __  | |__    ___  _ __   __ _ \n" +
        "    \\___ \\ | __| / _` || '_ \\  / _` | / _ \\| '_ \\ | '_ \\  / _ \\| '__| / _` |\n" +
        "    ____) || |_ | (_| || | | || (_| ||  __/| | | || |_) ||  __/| |   | (_| |\n" +
        "   |_____/  \\__| \\__,_||_| |_| \\__, | \\___||_| |_||_.__/  \\___||_|    \\__, |\n" +
        "                                __/ |                                  __/ |\n" +
        "                               |___/                                  |___/ \n";


    @GetMapping(produces = "text/plain")
    public String getCopyright() {
        return gameName + avTest;
    }

}
