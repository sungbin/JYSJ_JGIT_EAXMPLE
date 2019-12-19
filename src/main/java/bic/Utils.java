package bic;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;

import jiraCrawler.InvalidDomainException;
import jiraCrawler.InvalidProjectKeyException;
import jiraCrawler.JiraBugIssueCrawler;

public class Utils {
	final static Pattern keyPattern = Pattern.compile("\\[?(\\w+\\-\\d+)\\]?");

	public static Git gitClone(String REMOTE_URI)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {

		File repositoriesDir = new File("repositories" + File.separator + getProjectName(REMOTE_URI));
		Git git = null;
		if (repositoriesDir.exists()) {
			try {
				git = Git.open(repositoriesDir);
			} catch (RepositoryNotFoundException e) {
				if (repositoriesDir.delete()) {
					return gitClone(REMOTE_URI);
				}
			}
		} else {
			repositoriesDir.mkdirs();
			System.out.println("cloning..");
			git = Git.cloneRepository().setURI(REMOTE_URI).setDirectory(repositoriesDir).setCloneAllBranches(true)
					.call();
		}
		return git;
	}

	public static String getProjectName(String URI) {

		Pattern p = Pattern.compile(".*/(.+)\\.git");
		Matcher m = p.matcher(URI);
		m.find();
		return m.group(1);

	}

	public static HashSet<String> parseReference(String reference) throws IOException {
		HashSet<String> keywords = new HashSet<String>();
		File CSV = new File(reference);
		Reader reader = new FileReader(CSV);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(reader);

		for (CSVRecord record : records) {
			keywords.add(record.get(0));
		}
		return keywords;
	}

	public static Iterable<String> getBFCList(String jiraPackage, String jiraProjectKey, Iterable<RevCommit> commitList)
			throws InvalidDomainException, IOException, InvalidProjectKeyException {
		Iterable<String> bfsList = new ArrayList<>();

		JiraBugIssueCrawler crawler = new JiraBugIssueCrawler(jiraPackage, jiraProjectKey);
		String referenceAdress = crawler.getJiraBugs().getAbsolutePath();
		HashSet<String> keywords = Utils.parseReference(referenceAdress);
		for (RevCommit commit : commitList) {

			Matcher m = null;
			if (commit.getShortMessage().length() > 20)
				m = keyPattern.matcher(commit.getShortMessage().substring(0, 20)); // check if have keyword in // //
																					// Short message
			else
				m = keyPattern.matcher(commit.getShortMessage()); // check if have keyword in Short message
			if (!m.find())
				continue;
			String issueKey = m.group(1);
			if (keywords.contains(issueKey)) {
				((ArrayList) commitList).add(commit.getId().toString());
			}
		}
		return bfsList;
	}
}
