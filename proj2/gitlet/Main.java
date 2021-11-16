package gitlet;


import java.io.File;

import static gitlet.Utils.join;


/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {
    static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            errorCaller("Please enter a command.");
        }
        switch (args[0]) {
            default:
                errorCaller("No command with that name exists");
                break;
            case "merge":
                if (checkExists()) {
                    Repository.merge(args[1]);
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "reset":
                if (checkExists()) {
                    Repository.reset(args[1]);
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "rm-branch":
                if (checkExists()) {
                    Repository.rmBranch(args[1]);
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "status":
                if (checkExists()) {
                    Repository.status();
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "branch":
                if (checkExists()) {
                    Repository.branch(args[1]);
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "find":
                if (checkExists()) {
                    Repository.find(args[1]);
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "global-log":
                if (checkExists()) {
                    Repository.globalLog();
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "rm":
                if (checkExists()) {
                    Repository.rm(args[1]);
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "checkout":
                if (checkExists()) {
                    if (args.length == 3) {
                        Repository.checkout(args[2]);
                    } else if (args.length == 2) {
                        Repository.checkout(0, args[1]);
                    } else {
                        if (args[2].equals("--")) {
                            Repository.checkout(args[1], args[3]);
                        } else {
                            System.out.println("Incorrect operands.");
                            break;
                        }
                    }
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "log":
                if (checkExists()) {
                    Repository.log();
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "init":
                if (Repository.GITLET_DIR.exists()) {
                    errorCaller("A Gitlet version-control "
                            + "system already exists in the current directory.");
                } else {
                    Repository.setupPersistence();
                    Repository.init();
                    break;
                }
                break;
            case "add":
                if (checkExists()) {
                    Repository.add(args[1]);
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
            case "commit":
                if (checkExists()) {
                    if (args[1].equals("")) {
                        errorCaller("Please enter a commit message.");
                    } else {
                        Repository.commit(args[1], null);
                    }
                } else {
                    System.out.println("Not in an initialized Gitlet directory.");
                }
                break;
        }
    }

    private static boolean checkExists() {
        if (join(CWD, ".gitlet").exists()) {
            return true;
        }
        return false;
    }
    private static void errorCaller(String msg) {
        System.out.println(msg);
        System.exit(0);
    }

}
