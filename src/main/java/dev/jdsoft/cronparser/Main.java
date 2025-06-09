package dev.jdsoft.cronparser;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Invalid number of arguments. Argument should look like: \"*/15 0 1,15 * 1-5 /usr/bin/find\"");
        }
        var parser = new CronParser();
        var expression = parser.parse(args[0]);
        expression.prettyPrint();
    }
}