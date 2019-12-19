package bic;

import java.util.ArrayList;

import org.eclipse.jgit.revwalk.RevCommit;

public interface BICCollector {
	public ArrayList<BICLine> collect(Iterable<RevCommit> walk, Iterable<String> BFCCommitList);
}