package util;

import java.io.File;

public class ConversionTools {

	// return {"filename", ".ext"}
	// or     {"filename", ""}
	static String[] split_fileext(String filename) {
		int dotpos = filename.lastIndexOf(".");
		if (dotpos <= 0) {
			return new String[]{filename, ""};
		}
		String name = filename.substring(0, dotpos);
		String extension_incl_dot = filename.substring(dotpos);
		return new String[]{name, extension_incl_dot};
	}

	static File add_filename_suffix(File original_file, String suffix) {
		String[] filename_ext = split_fileext(original_file.getName());
		String new_name = filename_ext[0] + suffix + filename_ext[1];
		return new File(original_file.getParent(), new_name);
	}
}
