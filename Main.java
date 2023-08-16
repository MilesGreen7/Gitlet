package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Miles Green
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Commands cObj = new Commands();
        switch (args[0]) {
        case ("init"):
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.init();
            }
            break;
        case ("add"):
            if (args.length == 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.add(args);
            }
            break;
        case ("commit"):
            if (args.length == 1 || args[1].equals("")) {
                System.out.println("Please enter a commit message.");
            } else if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.commit(args[1]);
            }
            break;
        case ("checkout"):
            if (args.length == 3) {
                cObj.checkout1(args);
            } else if (args.length == 4) {
                cObj.checkout2(args);
            } else if (args.length == 2) {
                cObj.checkout3(args);
            } else {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            break;
        case ("rm"):
            if (args.length == 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.rm(args);
            }
            break;
        default:
            moreCommands(args);
        }
    }

    /** To condense Main.
     * @param args is the argument array */
    public static void moreCommands(String[] args) {
        Commands cObj = new Commands();
        switch (args[0]) {
        case ("log"):
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.log();
            }
            break;
        case ("find"):
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.find(args[1]);
            }
            break;
        case ("branch"):
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.branch(args[1]);
            }
            break;
        case ("rm-branch"):
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.rmBranch(args[1]);
            }
            break;
        case ("global-log"):
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.globalLog();
            }
            break;
        default:
            evenMoreCommannds(args);
        }
    }

    /** To condense Main.
     * @param args is string array */
    public static void evenMoreCommannds(String[] args) {
        Commands cObj = new Commands();
        switch (args[0]) {
        case ("status"):
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.status();
            }
            break;
        case ("reset"):
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                cObj.reset(args[1]);
            }
            break;
        default:
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
    }
}
