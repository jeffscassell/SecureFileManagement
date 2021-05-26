package com.sfm;
import java.util.LinkedList;

public class User
{
	private String username;
	private boolean upload;
	private boolean download;
	private boolean delete;
	private LinkedList<String> fileList;
	private Logger logger = new Logger(this);

	
	
	// used when uploading files, so that the feedback happens in real-time rather than all at once at the end of the upload
	public void addFile(String fileName) { fileList.add(fileName); }
	
	// used when deleting files
	public void removeFile(String fileName) { fileList.remove(fileName); }
	
	
	// logging methods
	public void logUpload(String fileName) { logger.uploaded(fileName); }
	public void logDownload(String fileName) { logger.downloaded(fileName); }
	public void logDelete(String fileName) { logger.deleted(fileName); }
	public void logLogin() { logger.loggedIn(); }
	public void logLogout() { logger.loggedOut(); }
	public void logError(String error) { logger.error(error); }
	
	public void logChangedPermission(User user, String field, boolean value)
	{ logger.changedPermission(user, field, value); }
	
	
	// getters
	public String getUsername()	{ return username; }
	public boolean hasUploadPermission() { return upload; }
	public boolean hasDownloadPermission() { return download; }
	public boolean hasDeletePermission() { return delete; }
	public LinkedList<String> getFileList() { return fileList; }
	
	
	// setters
	public void setUsername(String username) { this.username = username; }
	public void setUpload(boolean upload) { this.upload = upload; }
	public void setDownload(boolean download) { this.download = download; }
	public void setDelete(boolean delete) { this.delete = delete; }
	public void setFileList(LinkedList<String> fileList) { this.fileList = fileList; }
	
	
	@Override
	public String toString() { return username; }
}
