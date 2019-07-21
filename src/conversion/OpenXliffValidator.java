package conversion;

import xliff_model.ValidationError;
import java.util.ArrayList;
import xliff_model.FileTag;
import xliff_model.SegmentTag;
import xliff_model.SegmentTag.State;
import xliff_model.Tag;
import xliff_model.XliffTag;

public class OpenXliffValidator {

	public static ArrayList<ValidationError> validate(XliffTag xliffTag) {
		ArrayList<ValidationError> res = new ArrayList<>();
		for (FileTag fileTag : xliffTag.getFiles()) {
			for (SegmentTag segmentTag : fileTag.getSegmentsArray()) {
				boolean validateSegment = segmentTag.getStaged() || (segmentTag.getState() != State.INITIAL);
				if (validateSegment) {
					ArrayList<Tag> targetTags = segmentTag.getTargetText().getTags();
					if (targetTags.isEmpty() == false) {
						ArrayList<Tag> sourceTags = segmentTag.getSourceText().getTags();
						String compareResult = compareTagArrays(sourceTags, targetTags);
						if (compareResult.isEmpty() == false) {
							res.add(new ValidationError(segmentTag, compareResult));
						}
					}
				}

			}
		}
		return res;
	}

	static String tagTypeString(Tag t) {
		switch (t.getType()) {
			case START:
				return "start";
			case END:
				return "end";
			case EMPTY:
				return "standalone";
		}
		return "";
	}

	static String tagInfoString(Tag t) {
		return "'" + t.getLabel() + "' (" + tagTypeString(t) + ")";
	}

	static String compareTagArrays(ArrayList<Tag> srcTags, ArrayList<Tag> trgTags) {
		if (srcTags.size() != trgTags.size()) {
			return "The number of target tags (" + trgTags.size() + ") does not match the number of source tags (" + srcTags.size() + ")";
		}
		for (int i = 0; i < srcTags.size(); i++) {
			Tag srcTag = srcTags.get(i);
			Tag trgTag = trgTags.get(i);
			if (compareTags(srcTag, trgTag) == false) {
				return "Source/target tag mismatch at target tag #" + i + 1 + ": Expected " + tagInfoString(srcTag) + ", found " + tagInfoString(trgTag);
			}
		}
		return "";
	}

	static boolean compareTags(Tag tag1, Tag tag2) {
		return (tag1.getLabel().equals(tag2.getLabel())) && (tag1.getType() == tag2.getType());
	}
}
