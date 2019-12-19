package bic;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;

import jiraCrawler.InvalidDomainException;
import jiraCrawler.InvalidProjectKeyException;

public class Main {

	public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException,
			IOException, InvalidProjectKeyException, InvalidDomainException {

		if (args.length != 3) {
			System.out.println("Input must be: <GitHub_URI> <Jira-package> <Jira-project>");
		}

		String gitURL = args[0];
		String jiraPackage = args[1];
		String jiraProjectKey = args[2];

		// 1. git clone
		Git git = Utils.gitClone(gitURL);

		// 2. Commit collects
		Iterable<RevCommit> commitList = git.log().call();

		// 3. BFC collect
		Iterable<String> BFCList = Utils.getBFCList(jiraPackage, jiraProjectKey, commitList);

		// 4. BIC Algorithm
		BICCollector bicCollector = (walk, BICList) -> { // TODO: JY, SJ here. your part! remove lamda expression, and
															// make a class implemented on BICCollector interface
			ArrayList<BICLine> bicLines = new ArrayList<>();
			return bicLines;
		};

		// 5. BIC collect
		ArrayList<BICLine> bicLines = bicCollector.collect(commitList, BFCList);

	}

}
