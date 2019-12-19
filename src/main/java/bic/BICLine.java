package bic;

import org.eclipse.jgit.revwalk.RevCommit;

public class BICLine {
	public RevCommit commit;
	public String content;

	public BICLine(RevCommit commit, String content) {
		super();
		this.commit = commit;
		this.content = content;
	}
}
