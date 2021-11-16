package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 *
 * @author Neel Choudhary
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The folder wherein commits are stored
     */
    public static final File COMMIT_DIR = join(GITLET_DIR, "Commits");
    /**
     * Staging area which contains files which have been changed but not committed
     */
    public static final File SA_DIR = join(GITLET_DIR, "Staging Area");
    /**
     * Folder that contains blobs
     */
    public static final File BLOBS_DIR = join(GITLET_DIR, "Blobs");
    /**
     * File that contains list of branches
     */
    static TreeMap<String, String> branches = new TreeMap<>();
    static TreeMap<String, String> saAddFiles = new TreeMap<>();
    static TreeMap<String, String> saRmFiles = new TreeMap<>();
    static TreeMap<String, String> blobFiles = new TreeMap<>();

    public static void init() {
        Commit initial = new Commit("initial commit", null, null);
        byte[] content = serialize(initial);
        String commitHash = sha1(content);
        initial.setHash(commitHash);
        String masterPointer = "master";
        File masterInsert = join(GITLET_DIR, "master.txt");
        writeContents(masterInsert, masterPointer);
        File insert = join(COMMIT_DIR, commitHash);
        writeObject(insert, initial);
        File branchesFile = join(GITLET_DIR, "branches");
        branches.put(masterPointer, commitHash);
        writeObject(branchesFile, branches);
        saAddFiles = new TreeMap<>();
        saRmFiles = new TreeMap<>();
        File stagingAddingArea = join(GITLET_DIR, "SA_add_files.txt");
        writeObject(stagingAddingArea, saAddFiles);
        File stagingRemovingArea = join(GITLET_DIR, "SA_rm_files.txt");
        writeObject(stagingRemovingArea, saRmFiles);
        blobFiles = new TreeMap<>();
        File blobs = join(GITLET_DIR, "blobFiles.txt");
        writeObject(blobs, blobFiles);
    }

    public static void add(String fileInName) {
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        String currCommitHash = branches.get(masterPointer);
        File fileIn = join(CWD, fileInName);
        if (!fileIn.exists()) {
            errorCaller("File does not exist.");
        }
        String fileContents = readContentsAsString(fileIn);
        String fileHash = sha1(fileContents);
        File addStaging = join(GITLET_DIR, "SA_add_files.txt");
        saAddFiles = readObject(addStaging, TreeMap.class);
        File remStaging = join(GITLET_DIR, "SA_rm_files.txt");
        saRmFiles = readObject(remStaging, TreeMap.class);
        File blobs = join(GITLET_DIR, "blobFiles.txt");
        blobFiles = readObject(blobs, TreeMap.class);
        File currCommitFile = join(COMMIT_DIR, currCommitHash);
        Commit currCommit = readObject(currCommitFile, Commit.class);
        saRmFiles.remove(fileInName);
        if (currCommit.getFiles().containsKey(fileInName)) {
            String existingHash = currCommit.getFiles().get(fileInName);
            if (existingHash.equals(fileHash)) {
                if (saAddFiles.containsKey(fileInName)) {
                    saAddFiles.remove(fileInName);
                }
            } else {
                saAddFiles.put(fileInName, fileHash);
                File existing = join(SA_DIR, fileHash);
                String addedContents = readContentsAsString(fileIn);
                writeContents(existing, addedContents);
            }
        } else {
            File insert = join(SA_DIR, fileHash);
            writeContents(insert, fileContents);
            saAddFiles.put(fileIn.getName(), fileHash);
        }
        writeObject(addStaging, saAddFiles);
        writeObject(remStaging, saRmFiles);
        writeObject(blobs, blobFiles);
    }

    private static void errorCaller(String message) {
        System.out.println(message);
        System.exit(0);
    }

    public static void commit(String message, String branchParent) {
        //Name of commit should be a hash of something but dont know what ?
        //what is naming scheme for commits
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        String parentCommitHash = branches.get(masterPointer);
        File parentCommitFile = join(COMMIT_DIR, parentCommitHash);
        Commit parentCommit = readObject(parentCommitFile, Commit.class);
        Commit curr = new Commit(message, parentCommitHash, null);
        File addStaging = join(GITLET_DIR, "SA_add_files.txt");
        TreeMap<String, String> saFilesAdd = readObject(addStaging, TreeMap.class);
        File remStaging = join(GITLET_DIR, "SA_rm_files.txt");
        TreeMap<String, String> saFilesRem = readObject(remStaging, TreeMap.class);
        File blobs = join(GITLET_DIR, "blobFiles.txt");
        blobFiles = readObject(blobs, TreeMap.class);
        if (saFilesAdd.isEmpty() && saFilesRem.isEmpty()) {
            errorCaller("No changes added to the commit.");
        }
        HashMap<String, String> hereditary = new HashMap<>(parentCommit.getFiles());
        curr.setFiles(hereditary);
        if (!saFilesAdd.isEmpty()) {
            for (String key : saFilesAdd.keySet()) {
                String hashCode = saFilesAdd.get(key);
                curr.addToFiles(key, hashCode);
                File saCurr = join(SA_DIR, hashCode);
                String saCurrCont = readContentsAsString(saCurr);
                File transfer = join(BLOBS_DIR, hashCode);
                writeContents(transfer, saCurrCont);
                blobFiles.put(key, hashCode);
            }
        }
        if (!saFilesRem.isEmpty()) {
            for (String key : saFilesRem.keySet()) {
                curr.remFromFiles(key);
            }
        }
        if (branchParent != null) {
            curr.setParent2(branchParent);
        }
        String add = String.join("", curr.getFiles().values());
        String addedCommitHash = sha1(curr.getTimeStamp().toString(), curr.getMessage(),
                add, curr.getParent1());
        curr.setHash(addedCommitHash);
        branches.put(masterPointer, addedCommitHash);
        File commitInsert = join(COMMIT_DIR, addedCommitHash);
        writeObject(commitInsert, curr);
        writeObject(blobs, blobFiles);
        writeObject(branchesMapFile, branches);
        TreeMap<String, String> replace = new TreeMap<String, String>();
        writeObject(addStaging, replace);
        writeObject(remStaging, replace);
        clearStage();

    }

    public static void setupPersistence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        if (!BLOBS_DIR.exists()) {
            BLOBS_DIR.mkdir();
        }
        if (!SA_DIR.exists()) {
            SA_DIR.mkdir();
        }
        if (!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }

    }

    private static void clearStage() {
        List<String> stageFiles = plainFilenamesIn(SA_DIR);
        for (int i = 0; i < stageFiles.size(); i++) {
            restrictedDelete(stageFiles.get(i));
        }
    }

    public static void rm(String fileInName) {
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        String currCommitHash = branches.get(masterPointer);

        File stagingRm = join(GITLET_DIR, "SA_rm_files.txt");
        saRmFiles = readObject(stagingRm, TreeMap.class);

        File stagingAdd = join(GITLET_DIR, "SA_add_files.txt");
        saAddFiles = readObject(stagingAdd, TreeMap.class);

        File currCommitFile = join(COMMIT_DIR, currCommitHash);
        Commit currCommit = readObject(currCommitFile, Commit.class);

        if (!currCommit.getFiles().containsKey(fileInName) && !saAddFiles.containsKey(fileInName)) {
            errorCaller("No reason to remove the file.");
        }

        if (saAddFiles.containsKey(fileInName)) {
            saAddFiles.remove(fileInName);
        }
        if (currCommit.getFiles().containsKey(fileInName)) {
            File target = join(CWD, fileInName);
            if (target.exists()) {
                File fileIn = join(CWD, fileInName);
                String fileContents = readContentsAsString(fileIn);
                String fileHash = sha1(fileContents);
                saRmFiles.put(fileInName, fileHash);
                restrictedDelete(target);
            } else {
                saRmFiles.put(fileInName, "nothing");
            }
        }
        writeObject(stagingAdd, saAddFiles);
        writeObject(stagingRm, saRmFiles);
    }

    public static void log() {
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        String currCommitHash = branches.get(masterPointer);
        File currFile = join(COMMIT_DIR, currCommitHash);
        Commit currCommit = readObject(currFile, Commit.class);
        boolean cont = true;
        while (cont) {
            logPrinter(currCommit, currFile);
            if (currCommit.getParent1() == null) {
                break;
            }
            currFile = join(COMMIT_DIR, currCommit.getParent1());
            currCommit = readObject(currFile, Commit.class);
        }
    }

    private static void logPrinter(Commit currCommit, File currFile) {
        Formatter form = new Formatter();
        String print = form.format("Date: %ta %<tb %<te %<tH:%<tM:%<tS %<tY %<tz",
                currCommit.getTimeStamp()).toString();

        System.out.println("===");
        System.out.println("commit" + " " + currFile.getName());
        System.out.println(print);
        System.out.println(currCommit.getMessage());
        System.out.println();
    }

    public static void globalLog() {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        for (int i = 0; i < commits.size(); i++) {
            String currPointer = commits.get(i);
            File currFile = join(COMMIT_DIR, currPointer);
            Commit currCommit = readObject(currFile, Commit.class);
            logPrinter(currCommit, currFile);
        }
    }

    public static void rmBranch(String branchName) {
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        if (masterPointer.equals(branchName)) {
            errorCaller("Cannot remove the current branch.");
        }
        if (!branches.containsKey(branchName)) {
            errorCaller("A branch with that name does not exist.");
        }
        branches.remove(branchName);
        writeObject(branchesMapFile, branches);
    }

    public static void reset(String commitId) {
        List<String> commitIds = plainFilenamesIn(COMMIT_DIR);
        if (!commitIds.contains(commitId)) {
            errorCaller("No commit with that id exists.");
        }
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        File idCommitFile = join(COMMIT_DIR, commitId);
        Commit idCommit = readObject(idCommitFile, Commit.class);
        String masterId = branches.get(masterPointer);
        File masterCommitFile = join(COMMIT_DIR, masterId);
        Commit masterCommit = readObject(masterCommitFile, Commit.class);
        List<String> workingFiles = plainFilenamesIn(CWD);
        for (String key : idCommit.getFiles().keySet()) {
            if (!masterCommit.contains(key) && workingFiles.contains(key)) {
                errorCaller("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
        for (String fileName : idCommit.getFiles().keySet()) {
            checkout(commitId, fileName);
        }
        for (String fileName : masterCommit.getFiles().keySet()) {
            if (!idCommit.getFiles().containsKey(fileName)) {
                File target = join(CWD, fileName);
                restrictedDelete(target);
            }
        }
        branches.put(masterPointer, commitId);
        writeObject(branchesMapFile, branches);
        File addStaging = join(GITLET_DIR, "SA_add_files.txt");
        File remStaging = join(GITLET_DIR, "SA_rm_files.txt");
        TreeMap<String, String> replace = new TreeMap<String, String>();
        writeObject(addStaging, replace);
        writeObject(remStaging, replace);
        clearStage();
    }

    public static void merge(String branchName) {
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        File stagingRm = join(GITLET_DIR, "SA_rm_files.txt");
        saRmFiles = readObject(stagingRm, TreeMap.class);
        File stagingAdd = join(GITLET_DIR, "SA_add_files.txt");
        saAddFiles = readObject(stagingAdd, TreeMap.class);
        if (!branches.containsKey(branchName)) {
            errorCaller("A branch with that name does not exist.");
        }
        if (masterPointer.equals(branchName)) {
            errorCaller("Cannot merge a branch with itself.");
        }
        if (!saRmFiles.isEmpty() || !saAddFiles.isEmpty()) {
            errorCaller("You have uncommitted changes.");
        }
        String givenId = branches.get(branchName);
        Commit given = readObject(join(COMMIT_DIR, givenId), Commit.class);
        String currId = branches.get(masterPointer);
        Commit curr = readObject(join(COMMIT_DIR, currId), Commit.class);
        String splitId = splitFinder(branches.get(branchName), branches.get(masterPointer));
        Commit splitPoint = readObject(join(COMMIT_DIR, splitId), Commit.class);
        HashSet<String> ancestry = currentLineageTracker(join(COMMIT_DIR, currId), currId);
        HashSet<String> ancestryGiven = currentLineageTracker(join(COMMIT_DIR, givenId), givenId);
        if (ancestry.contains(givenId)) {
            errorCaller("Given branch is an ancestor of the current branch.");
        }
        if (ancestryGiven.contains(currId)) {
            checkout(0, branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        List<String> workingFiles = plainFilenamesIn(CWD);
        for (String key : workingFiles) {
            if (!curr.getFiles().containsKey(key)) {
                errorCaller("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
        caseOne(given, curr, splitPoint, givenId);
        caseFive(given, curr, splitPoint, givenId);
        caseSix(given, curr, splitPoint);
        boolean conflict = changedInBothChecker(given, curr, splitPoint);
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
        commit("Merged " + branchName + " into " + masterPointer + ".", givenId);
    }

    private static void caseOne(Commit given, Commit curr, Commit split, String givenId) {
        for (String fileName : given.getFiles().keySet()) {
            if (split.getFiles().containsKey(fileName)) {
                String givenFileHash = given.getFiles().get(fileName);
                String currFileHash = curr.getFiles().get(fileName);
                String splitFileHash = split.getFiles().get(fileName);
                if (!givenFileHash.equals(splitFileHash)
                        && curr.getFiles().containsKey(fileName)
                        && currFileHash.equals(splitFileHash)) {
                    checkout(givenId, fileName);
                    add(fileName);
                }
            }
        }
    }

    private static void caseFive(Commit given, Commit curr, Commit split, String givenId) {
        for (String fileName : given.getFiles().keySet()) {
            if (!split.getFiles().containsKey(fileName) && !curr.getFiles().containsKey(fileName)) {
                checkout(givenId, fileName);
                add(fileName);
            }
        }
    }

    private static void caseSix(Commit given, Commit curr, Commit split) {
        for (String fileName : split.getFiles().keySet()) {
            String currFileHash = curr.getFiles().get(fileName);
            String splitFileHash = split.getFiles().get(fileName);
            if (!given.getFiles().containsKey(fileName)
                    && Objects.equals(currFileHash, splitFileHash)) {
                rm(fileName);
                File target = join(CWD, fileName);
                if (target.exists()) {
                    restrictedDelete(target);
                }
            }
        }
    }

    private static boolean changedInBothChecker(Commit given, Commit curr, Commit split) {
        boolean ret = false;
        for (String fileName : split.getFiles().keySet()) {
            String givenFileHash = given.getFiles().get(fileName);
            String currFileHash = curr.getFiles().get(fileName);
            String splitFileHash = split.getFiles().get(fileName);
            if (givenFileHash == null && currFileHash == null) {
                return false;
            }
            if (!Objects.equals(currFileHash, splitFileHash)
                    && !given.getFiles().containsKey(fileName) && currFileHash != null) {
                conflictArises("empty", currFileHash, fileName);
                ret = true;
            }
            if (!Objects.equals(givenFileHash, splitFileHash)
                    && !curr.getFiles().containsKey(fileName) && givenFileHash != null) {
                conflictArises(givenFileHash, "empty", fileName);
                ret = true;
            }
            if (!Objects.equals(currFileHash, splitFileHash)
                    && !Objects.equals(givenFileHash, splitFileHash)
                    && !Objects.equals(givenFileHash, currFileHash)
                    && givenFileHash != null && currFileHash != null) {
                conflictArises(givenFileHash, currFileHash, fileName);
                ret = true;
            }
        }
        for (String currFile : curr.getFiles().keySet()) {
            if (given.getFiles().containsKey(currFile)
                    && !split.getFiles().containsKey(currFile)) {
                String currFileHash = curr.getFiles().get(currFile);
                String givenFileHash = given.getFiles().get(currFile);
                if (!Objects.equals(currFileHash, givenFileHash)) {
                    conflictArises(givenFileHash, currFileHash, currFile);
                    ret = true;
                }
            }
        }
        return ret;
    }

    private static void conflictArises(String givenFileHash,
                                       String currFileHash, String fileInConflict) {
        File currFile = join(BLOBS_DIR, currFileHash);
        File givenFile = join(BLOBS_DIR, givenFileHash);
        String currContents = "";
        String givenContents = "";
        if (currFile.exists()) {
            currContents = readContentsAsString(currFile);
        }
        if (givenFile.exists()) {
            givenContents = readContentsAsString(givenFile);
        }
        String ret = "<<<<<<< HEAD" + "\n"
                + currContents + "=======" + "\n"
                + givenContents + ">>>>>>>" + "\n";
        File conflict = join(CWD, fileInConflict);
        writeContents(conflict, ret);
        add(fileInConflict);
    }

    private static String splitFinder(String givenHash, String currHash) {
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        if (givenHash.equals(currHash)) {
            return masterPointer;
        }
        File currCommitFile = join(COMMIT_DIR, currHash);
        File givenCommitFile = join(COMMIT_DIR, givenHash);
        Commit currCommit = readObject(currCommitFile, Commit.class);
        Commit givenCommit = readObject(givenCommitFile, Commit.class);
        HashSet<String> currSet = currentLineageTracker(currCommitFile, currHash);
        Queue<String> givenQueue = new ArrayDeque<>();
        givenQueue.add(givenHash);
        while (!givenQueue.isEmpty()) {
            String id = givenQueue.remove();
            if (currSet.contains(id)) {
                return id;
            }
            givenCommitFile = join(COMMIT_DIR, id);
            givenCommit = readObject(givenCommitFile, Commit.class);
            if (givenCommit.getParent1() != null) {
                givenQueue.add(givenCommit.getParent1());
            }
            if (givenCommit.getParent2() != null) {
                givenQueue.add(givenCommit.getParent2());
            }
        }
        String splitId = "";
        return splitId;
    }

    private static HashSet currentLineageTracker(File child, String hash) {
        Queue<String> currQueue = new ArrayDeque<>();
        HashSet<String> ret = new HashSet<>();
        File commitFile = child;
        Commit curr = readObject(child, Commit.class);
        currQueue.add(hash);
        while (!currQueue.isEmpty()) {
            String id = currQueue.remove();
            ret.add(id);
            commitFile = join(COMMIT_DIR, id);
            curr = readObject(commitFile, Commit.class);
            if (curr.getParent1() != null) {
                currQueue.add(curr.getParent1());
            }
            if (curr.getParent2() != null) {
                currQueue.add(curr.getParent2());
            }
        }
        return ret;
    }

    public static void status() {
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        System.out.println("=== Branches ===");
        for (String currBranch : branches.keySet()) {
            if (currBranch.equals(masterPointer)) {
                System.out.print("*");
            }
            System.out.println(currBranch);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        File addStage = join(GITLET_DIR, "SA_add_files.txt");
        saAddFiles = readObject(addStage, TreeMap.class);
        File remStage = join(GITLET_DIR, "SA_rm_files.txt");
        saRmFiles = readObject(remStage, TreeMap.class);
        for (String key : saAddFiles.keySet()) {
            System.out.println(key);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String key : saRmFiles.keySet()) {
            System.out.println(key);
        }
        String currCommitHash = branches.get(masterPointer);
        File currCommitFile = join(COMMIT_DIR, currCommitHash);
        Commit currCommit = readObject(currCommitFile, Commit.class);
        List<String> workingFiles = plainFilenamesIn(CWD);
        Iterator keyIterCommitTracking = currCommit.getFiles().keySet().iterator();
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String fileName : currCommit.getFiles().keySet()) {
            File temp = join(CWD, fileName);
            if (!saRmFiles.containsKey(fileName)
                    && currCommit.getFiles().containsKey(fileName) && !temp.exists()) {
                System.out.println(fileName + " (deleted)");
            }
        }
        for (String fileName : workingFiles) {
            File temp = join(CWD, fileName);
            if (!temp.exists() && saAddFiles.containsKey(fileName)) {
                System.out.println(fileName + " (deleted)");
            }
            String fileContents = readContentsAsString(temp);
            String fileHash = sha1(fileContents);
            if (saAddFiles.containsKey(fileName) && !saAddFiles.get(fileName).equals(fileHash)) {
                System.out.println(fileName + " (modified)");
            }
            if (currCommit.getFiles().containsKey(fileName)
                    && !Objects.equals(fileHash, currCommit.getFiles().get(fileName))
                    && !saAddFiles.containsKey(fileName)) {
                System.out.println(fileName + " (modified)");
            }
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String fileName : workingFiles) {
            File temp = join(CWD, fileName);
            if (saRmFiles.containsKey(fileName)
                    && temp.exists() && !saAddFiles.containsKey(fileName)) {
                System.out.println(fileName);
            }
            if (!currCommit.getFiles().containsKey(fileName)
                    && !saAddFiles.containsKey(fileName)) {
                System.out.println(fileName);
            }
        }
    }

    public static void branch(String branchNameIn) {
        File branchesMapFile = join(GITLET_DIR, "branches");
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        branches = readObject(branchesMapFile, TreeMap.class);
        String currCommitHash = branches.get(masterPointer);
        if (branches.containsKey(branchNameIn)) {
            errorCaller("A branch with that name already exists.");
        }
        branches.put(branchNameIn, currCommitHash);
        writeObject(branchesMapFile, branches);
    }

    public static void find(String messageIn) {
        List<String> validIds = new ArrayList<String>();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        for (int i = 0; i < commits.size(); i++) {
            String currPointer = commits.get(i);
            File currFile = join(COMMIT_DIR, currPointer);
            Commit currCommit = readObject(currFile, Commit.class);
            if (currCommit.getMessage().equals(messageIn)) {
                validIds.add(currPointer);
            }
        }
        if (!validIds.isEmpty()) {
            for (int i = 0; i < validIds.size(); i++) {
                System.out.println(validIds.get(i));
            }
        } else {
            errorCaller("Found no commit with that message.");
        }
    }

    public static void checkout(String fileName) {
        File staging = join(GITLET_DIR, "SA_add_files.txt");
        TreeMap<String, String> saFiles = readObject(staging, TreeMap.class);
        File blobs = join(GITLET_DIR, "blobFiles.txt");
        blobFiles = readObject(blobs, TreeMap.class);
        File insert = join(CWD, fileName);
        File masterFile = join(GITLET_DIR, "master.txt");
        String masterPointer = readContentsAsString(masterFile);
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        String currCommitHash = branches.get(masterPointer);
        File currCommitFile = join(COMMIT_DIR, currCommitHash);
        Commit currCommit = readObject(currCommitFile, Commit.class);
        if (!currCommit.getFiles().containsKey(fileName)) {
            errorCaller("File does not exist in that commit.");
        }
        String hash = currCommit.getFromFiles(fileName);
        File fileInCommit = join(BLOBS_DIR, hash);
        String contents = readContentsAsString(fileInCommit);
        writeContents(insert, contents);
        writeObject(staging, saFiles);
        writeObject(blobs, blobFiles);
    }

    public static void checkout(int classifier, String branchNameIn) {
        File branchesMapFile = join(GITLET_DIR, "branches");
        branches = readObject(branchesMapFile, TreeMap.class);
        if (branches.containsKey(branchNameIn)) {
            File masterFile = join(GITLET_DIR, "master.txt");
            String masterPointer = readContentsAsString(masterFile);
            String currCommitHash = branches.get(masterPointer);
            File currCommitFile = join(COMMIT_DIR, currCommitHash);
            Commit currCommit = readObject(currCommitFile, Commit.class);
            String branchCommitHash = branches.get(branchNameIn);
            File branchCommitFile = join(COMMIT_DIR, branchCommitHash);
            Commit branchCommit = readObject(branchCommitFile, Commit.class);
            List<String> overwritten = new ArrayList<>();
            List<String> workingFiles = plainFilenamesIn(CWD);
            if (branchNameIn.equals(masterPointer)) {
                errorCaller("No need to checkout the current branch.");
            }
            boolean failureCheck = false;
            for (int i = 0; i < workingFiles.size(); i++) {
                String workingFileName = workingFiles.get(i);
                File curr = join(CWD, workingFileName);
                if (failureCheck) {
                    if (!branchCommit.getFiles().containsKey(workingFileName)) {
                        restrictedDelete(curr);
                    } else {
                        overwritten.add(workingFileName);
                        File insert = join(BLOBS_DIR,
                                branchCommit.getFiles().get(workingFileName));
                        String contentInsert = readContentsAsString(insert);
                        writeContents(curr, contentInsert);
                    }
                } else {
                    if (branchCommit.getFiles().containsKey(workingFileName)
                            && !currCommit.getFiles().containsKey(workingFileName)) {
                        errorCaller("There is an untracked file in the way; "
                                + "delete it, or add and commit it first.");
                    }
                    if (i == workingFiles.size() - 1) {
                        failureCheck = true;
                        i = -1;
                    }
                }
            }
            Iterator keyIter = branchCommit.getFiles().keySet().iterator();
            while (keyIter.hasNext()) {
                String curr = "" + keyIter.next();
                if (!overwritten.contains(curr)) {
                    File insertion = join(CWD, curr);
                    File contentFile = join(BLOBS_DIR, branchCommit.getFiles().get(curr));
                    String contentInsert = readContentsAsString(contentFile);
                    writeContents(insertion, contentInsert);
                }
            }
            masterPointer = branchNameIn;
            writeContents(masterFile, masterPointer);
        } else {
            errorCaller("No such branch exists.");
        }
        clearStage();
    }

    public static void checkout(String commitId, String fileName) {
        File insert = join(CWD, fileName);
        List<String> commitIds = plainFilenamesIn(COMMIT_DIR);
        for (String id : commitIds) {
            if (id.startsWith(commitId)) {
                commitId = id;
            }
        }
        File commitFile = join(COMMIT_DIR, commitId);
        if (!commitFile.exists()) {
            errorCaller("No commit with that id exists.");
        }
        Commit currCommit = readObject(commitFile, Commit.class);
        String hash = currCommit.getFromFiles(fileName);
        if (hash == null) {
            errorCaller("File does not exist in that commit.");
        }
        File fileInCommit = join(BLOBS_DIR, hash);
        String contents = readContentsAsString(fileInCommit);
        writeContents(insert, contents);
    }

}
