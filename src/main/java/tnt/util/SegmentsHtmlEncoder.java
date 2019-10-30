package tnt.util;

import java.util.ArrayList;
import tnt.xliff_model.SegmentTag;
import tnt.xliff_model.XliffTag;

public class SegmentsHtmlEncoder {

	public String encode(XliffTag xliffTag) {
		ArrayList<SegmentTag> segmentTags = xliffTag.getSegmentsArray();
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n");
		sb.append("<title>").append(xliffTag.getFile().getName()).append("</title>\n");
		sb.append("<body>\n");
		sb.append("<table border=\"1\">\n");
		sb.append("<tr>\n");
		sb.append("<th>Id</th>\n");
		sb.append("<th>Source text</th>\n");
		sb.append("<th>Target text</th>\n");
		sb.append("<th>State</th>\n");
		sb.append("</tr>\n");
		for (SegmentTag st : segmentTags) {
			String sourceText = st.getSourceText().getTextContent();
			String targetText = st.getTargetText().getTextContent();
			sb.append("<tr>\n");
			sb.append("<td>").append(st.getId()).append("</td>\n");
			sb.append("<td>").append(sourceText).append("</td>\n");
			sb.append("<td>").append(targetText).append("</td>\n");
			sb.append("<td>").append(st.getState()).append("</td>\n");
			sb.append("</tr>\n");
		}
		sb.append("</table>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
	}
}
