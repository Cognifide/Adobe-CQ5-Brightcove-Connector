package com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums;

public enum UploadStatusEnum {
	UPLOADING,  // File is still uploading.
	PROCESSING, // Upload complete; being processed.
	COMPLETE,   // Upload and processing complete.
	ERROR;       // Error in upload or processing.
}
