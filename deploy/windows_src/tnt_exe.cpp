#include <windows.h>
#include <sstream>

int main() {
	STARTUPINFOW si;
	PROCESS_INFORMATION pi;
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	ZeroMemory(&pi, sizeof(pi));
	LPCWSTR cmd = L"jre\\bin\\java.exe";
	WCHAR args[] = L"jre\\bin\\java.exe --class-path \"lib/*\" editor.Application";
	if (CreateProcessW(cmd, args, NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi) == 0) {
		std::wstringstream ss;
		ss << "Could not run " << cmd << std::endl;
		ss << "error code: " << GetLastError() << std::endl;
		ss << "command line: " << args << std::endl;
		MessageBoxExW(NULL, ss.str().c_str(), L"Message", MB_OK | MB_ICONERROR, 0);
		return 0;
	}
	CloseHandle(pi.hProcess);
	CloseHandle(pi.hThread);
	return 0;
}
