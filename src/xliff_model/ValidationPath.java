package xliff_model;

public class ValidationPath {

	public final String fileId;
	public final String unitId;
	public final String segmentId;
	public final String codeId;

	public ValidationPath(String fileId, String unitId, String segmentId, String codeId) {
		this.fileId = fileId;
		this.unitId = unitId;
		this.segmentId = segmentId;
		this.codeId = codeId;
	}

	public ValidationPath() {
		this.fileId = "";
		this.unitId = "";
		this.segmentId = "";
		this.codeId = "";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("file=").append(fileId);
		return "file='" + fileId + "', unit='" + unitId + "', segment='" + segmentId + "', tag='" + codeId + "'";
	}
}
