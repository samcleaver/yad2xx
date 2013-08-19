#include <stdio.h>
#include <stdlib.h>

#if defined (_WIN32)
#include <windows.h>
#endif

#include "net_sf_yad2xx_FTDIInterface.h"
#include "ftd2xx.h"

/*
 * Utility method to make it easier to handle failures.
 *
 * Creates an FTDIException, sets the status and function name.
 */
void ThrowFTDIException(JNIEnv * env, const jint ftStatus, const char * functionName) {

	// Lookup exception class
	jclass exceptionCls = (*env)->FindClass(env, "net/sf/yad2xx/FTDIException");
	if (exceptionCls == NULL) {
		return;  // Exception thrown
	}

	// Get the constructor for FTDIException(int, String)
	jmethodID cid = (*env)->GetMethodID(env, exceptionCls, "<init>", "(ILjava/lang/String;)V");
	if (cid == NULL) {
		return;  // Exception thrown
	}

	// Convert C string to Java
	jstring jFuncName = (*env)->NewStringUTF(env, functionName);
	if (jFuncName == NULL) {
		return; // Exception thrown
	}

	// Create and throw the exception
	jthrowable theException = (*env)->NewObject(env, exceptionCls, cid, ftStatus, jFuncName);
	if (theException != NULL) {
		(*env)->Throw(env, theException);
	}
	(*env)->DeleteLocalRef(env, exceptionCls);
	(*env)->DeleteLocalRef(env, jFuncName);
	(*env)->DeleteLocalRef(env, theException);
}


/*
 * Close an open device.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    close
 * Signature: (Lnet/sf/yad2xx/Device;)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_close
  (JNIEnv * env, jobject iFace, jobject device) {
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	// Get Device.class
	jclass deviceCls = (*env)->GetObjectClass(env, device);
	if (deviceCls == NULL) {
		return; // Exception thrown
	}

	// get device field ftHandle
	jfieldID handleID = (*env)->GetFieldID(env, deviceCls, "ftHandle", "J");
	if (handleID == NULL) {
		return; // Exception thrown
	}
	ftHandle = (FT_HANDLE) (*env)->GetLongField(env, device, handleID);

	ftStatus = FT_Close(ftHandle);

	if (ftStatus == FT_OK) {

		// update device flags
		jint flags;
		jfieldID flagsID = (*env)->GetFieldID(env, deviceCls, "flags", "I");
		if (flagsID == NULL) {
			return; // Exception thrown
		}
		flags = (*env)->GetIntField(env, device, flagsID);
		flags &= ~(FT_FLAGS_OPENED);
		(*env)->SetIntField(env, device, flagsID, flags);

		// update device handle
		(*env)->SetLongField(env, device, handleID, 0);

	} else {
		ThrowFTDIException(env, ftStatus, "FT_Close");
	}
}


/*
 * This function clears the Data Terminal Ready (DTR) control signal.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    clrDtr
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_clrDtr
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_ClrDtr(ftHandle);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_ClrDtr");
		return;
	}
}


/*
 * This function clears the Request To Send (RTS) control signal.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    clrRts
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_clrRts
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_ClrRts(ftHandle);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_ClrRts");
		return;
	}
}


/*
 * Returns the number of D2XX devices attached.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    getDeviceCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_yad2xx_FTDIInterface_getDeviceCount
  (JNIEnv * env, jobject iFace)
{
	FT_STATUS ftStatus;
	DWORD dwNumDevs;

	ftStatus = FT_CreateDeviceInfoList(&dwNumDevs);
	if (ftStatus == FT_OK) {
		return dwNumDevs;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_CreateDeviceInfoList");
		return 0;
	}
}


/*
 * Returns the D2XX DLL version number.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    getLibraryVersionInt
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_yad2xx_FTDIInterface_getLibraryVersionInt
  (JNIEnv * env, jobject iFace)
{
	FT_STATUS ftStatus;
	DWORD dwVersion;

	ftStatus = FT_GetLibraryVersion(&dwVersion);
	if (ftStatus == FT_OK) {
		return dwVersion;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_GetLibraryVersion");
		return 0;
	}
}


/**
 * Combines FT_CreateDeviceInfoList and FT_GetDeviceInfoList.
 *
 * Copies values into individual Device objects.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    getDevices
 * Signature: ()[Lnet/sf/yad2xx/Device;
 */
JNIEXPORT jobjectArray JNICALL Java_net_sf_yad2xx_FTDIInterface_getDevices
  (JNIEnv * env, jobject iFace)
{
	FT_STATUS ftStatus;
	DWORD dwNumDevs;

	// How many devices are attached?
	ftStatus = FT_CreateDeviceInfoList(&dwNumDevs);
	if (ftStatus != FT_OK) {
		ThrowFTDIException(env, ftStatus, "FT_CreateDeviceInfoList");
		return NULL;
	}

	// Lookup Device.class
	jclass deviceCls = (*env)->FindClass(env, "net/sf/yad2xx/Device");
	if (deviceCls == NULL) {
		return NULL;  // Exception thrown
	}

	// Allocate an array to hold the correct number of attached Devices
	jobjectArray devices = (*env)->NewObjectArray(env, dwNumDevs, deviceCls, NULL);
	if (devices == NULL) {
		return NULL;  // OutOfMemoryError thrown
	}

	if (dwNumDevs > 0) {
		// allocate storage for list based on numDevs
		FT_DEVICE_LIST_INFO_NODE * devInfo = (FT_DEVICE_LIST_INFO_NODE*) malloc(sizeof(FT_DEVICE_LIST_INFO_NODE) * dwNumDevs);

		// get the device information list
		ftStatus = FT_GetDeviceInfoList(devInfo, &dwNumDevs);
		if (ftStatus == FT_OK) {

			// Get the constructor for Device(int,int,int,int,int,String,String,long)
			jmethodID cid = (*env)->GetMethodID(env, deviceCls, "<init>", "(Lnet/sf/yad2xx/FTDIInterface;IIIIILjava/lang/String;Ljava/lang/String;J)V");
			if (cid == NULL) {
				return NULL;  // Exception thrown
			}

			int i;
			for (i = 0; i < dwNumDevs; i++) {

				// Convert C strings to Java
				jstring jSerial = (*env)->NewStringUTF(env, devInfo[i].SerialNumber);
				if (jSerial == NULL) {
					return NULL; // Exception thrown
				}
				jstring jDesc = (*env)->NewStringUTF(env, devInfo[i].Description);
				if (jDesc == NULL) {
					return NULL; // Exception thrown
				}

				// Construct the Device
				jobject device = (*env)->NewObject(env, deviceCls, cid, iFace, i, devInfo[i].Flags, devInfo[i].Type, devInfo[i].ID,
						devInfo[i].LocId, jSerial, jDesc, devInfo[i].ftHandle);
				if (device == NULL) {
					return NULL; // Exception thrown
				}

				// insert into result array
				(*env)->SetObjectArrayElement(env, devices, i, device);

				(*env)->DeleteLocalRef(env, jSerial);
				(*env)->DeleteLocalRef(env, jDesc);
				(*env)->DeleteLocalRef(env, device);
			}

		}

		free(devInfo);
	}

	return devices;
}


/*
 * Gets the instantaneous value of the data bus.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    getBitMode
 * Signature: (J)B
 */
JNIEXPORT jbyte JNICALL Java_net_sf_yad2xx_FTDIInterface_getBitMode
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;
	UCHAR BitMode;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_GetBitMode(ftHandle, &BitMode);

	if (ftStatus == FT_OK) {
		return (jbyte)BitMode;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_GetBitMode");
		return 0;
	}
}


/*
 * Get the current value of the latency timer.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    getLatencyTimer
 * Signature: (J)B
 */
JNIEXPORT jbyte JNICALL Java_net_sf_yad2xx_FTDIInterface_getLatencyTimer
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;
	UCHAR LatencyTimer;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_GetLatencyTimer(ftHandle, &LatencyTimer);

	if (ftStatus == FT_OK) {
		return (jbyte)LatencyTimer;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_GetLatencyTimer");
		return 0;
	}
}


/*
 * Gets the number of bytes in the receive queue.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    getQueueStatus
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_net_sf_yad2xx_FTDIInterface_getQueueStatus
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;
	DWORD     dwNumBytes;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_GetQueueStatus(ftHandle, &dwNumBytes);

	if (ftStatus == FT_OK) {
		return dwNumBytes;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_GetQueueStatus");
		return 0;
	}
}


/*
 * Open the device and return a handle which will be used for subsequent accesses.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    open
 * Signature: (Lnet/sf/yad2xx/Device;)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_open
  (JNIEnv * env, jobject iFace, jobject device) {
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;
	DWORD dwDeviceIndex;

	jclass deviceCls = (*env)->GetObjectClass(env, device);
	if (deviceCls == NULL) {
		return; // Exception thrown
	}

	// get device index
	jfieldID indexID = (*env)->GetFieldID(env, deviceCls, "index", "I");
	if (indexID == NULL) {
		return; // Exception thrown
	}
	dwDeviceIndex = (*env)->GetIntField(env, device, indexID);

	ftStatus = FT_Open(dwDeviceIndex, &ftHandle);

	if (ftStatus == FT_OK) {

		// update device flags
		jint flags;
		jfieldID flagsID = (*env)->GetFieldID(env, deviceCls, "flags", "I");
		if (flagsID == NULL) {
			return; // Exception thrown
		}
		flags = (*env)->GetIntField(env, device, flagsID);
		flags |= FT_FLAGS_OPENED;
		(*env)->SetIntField(env, device, flagsID, flags);

		// update device handle
		jfieldID handleID = (*env)->GetFieldID(env, deviceCls, "ftHandle", "J");
		if (handleID == NULL) {
			return; // Exception thrown
		}
		(*env)->SetLongField(env, device, handleID, (jlong) ftHandle);

	} else {
		ThrowFTDIException(env, ftStatus, "FT_Open");
	}
}


/*
 * Read data from the device.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    read
 * Signature: (J[BI)I
 */
JNIEXPORT jint JNICALL Java_net_sf_yad2xx_FTDIInterface_read
  (JNIEnv * env, jobject iFace, jlong handle, jbyteArray buffer, jint buffLength)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;
	DWORD     dwNumBytesToRead;
	DWORD     dwNumBytesRead;
    jbyte     inBuff[buffLength];

	ftHandle = (FT_HANDLE) handle;
	dwNumBytesToRead = buffLength;

    ftStatus = FT_Read(ftHandle, &inBuff, dwNumBytesToRead, &dwNumBytesRead);

	if (ftStatus == FT_OK) {
		(*env)->SetByteArrayRegion(env, buffer, 0, (jsize) dwNumBytesRead, inBuff);
		return dwNumBytesRead;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_Read");
		return 0;
	}

}


/*
 * This function sends a reset command to the device.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    reset
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_reset
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_ResetDevice(ftHandle);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_ResetDevice");
		return;
	}
}


/*
 * This function sets the baud rate for the device.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setBaudRate
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setBaudRate
  (JNIEnv * env, jobject iFace, jlong handle, jint baudRate)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetBaudRate(ftHandle, (DWORD) baudRate);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetBaudRate");
		return;
	}
}


/*
 * Enables different chip modes.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setBitMode
 * Signature: (JBB)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setBitMode
  (JNIEnv * env, jobject iFace, jlong handle, jbyte mask, jbyte mode)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetBitMode(ftHandle, mask, mode);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetBitMode");
		return;
	}
}


/*
 * Set special characters for the device.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setChars
 * Signature: (JCZCZ)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setChars
  (JNIEnv * env, jobject iFace, jlong handle, jchar event, jboolean eventEnable, jchar error, jboolean errorEnable)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetChars(ftHandle,
			event,
			eventEnable ? 1 : 0,
			error,
			errorEnable ? 1 : 0);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetChars");
		return;
	}
}


/*
 * This function sets the Data Terminal Ready (DTR) control signal.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setDtr
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setDtr
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetDtr(ftHandle);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetDtr");
		return;
	}
}


/*
 * Set the latency timer value.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setLatencyTimer
 * Signature: (JB)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setLatencyTimer
  (JNIEnv * env, jobject iFace, jlong handle, jbyte timer)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetLatencyTimer(ftHandle, (UCHAR) timer);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetLatencyTimer");
		return;
	}
}


/*
 * This function sets the Request To Send (RTS) control signal.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setRts
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setRts
  (JNIEnv * env, jobject iFace, jlong handle)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetRts(ftHandle);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetRts");
		return;
	}
}


/*
 * This function sets the read and write timeouts for the device.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setTimeouts
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setTimeouts
  (JNIEnv * env, jobject iFace, jlong handle, jint readTimeout, jint writeTimeout)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetTimeouts(ftHandle, (DWORD) readTimeout, (DWORD) writeTimeout);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetTimeouts");
		return;
	}
}


/*
 * Set the USB request transfer size.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setUSBParameters
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setUSBParameters
  (JNIEnv * env, jobject iFace, jlong handle, jint inTransferSize, jint outTransferSize)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;

	ftHandle = (FT_HANDLE) handle;
	ftStatus = FT_SetUSBParameters(ftHandle, (DWORD) inTransferSize, (DWORD) outTransferSize);

	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetUSBParameters");
		return;
	}
}


/*
 * A command to include a custom VID and PID combination within the internal device list table.
 * This will allow the driver to load for the specified VID and PID combination.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    setVidPid
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_net_sf_yad2xx_FTDIInterface_setVidPid
  (JNIEnv * env, jobject iFace, jint vid, jint pid)
{
#if defined (_WIN32)

	// Function is not defined on Windows platform, no-op instead.

	return;

#else

	FT_STATUS ftStatus;

	ftStatus = FT_SetVIDPID(vid, pid);
	if (ftStatus == FT_OK) {
		return;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_SetVIDPID");
		return;
	}

#endif

}


/*
 * Write data to the device.
 *
 * Class:     net_sf_yad2xx_FTDIInterface
 * Method:    write
 * Signature: (J[BI)I
 */
JNIEXPORT jint JNICALL Java_net_sf_yad2xx_FTDIInterface_write
  (JNIEnv * env, jobject iFace, jlong handle, jbyteArray buffer, jint buffLength)
{
	FT_HANDLE ftHandle;
	FT_STATUS ftStatus;
	DWORD     dwByteCount;
	DWORD     dwBytesWritten;
	jbyte     writeBuffer[buffLength];

	dwByteCount = (DWORD) buffLength;
	ftHandle = (FT_HANDLE) handle;
	(*env)->GetByteArrayRegion(env, buffer, 0, dwByteCount, writeBuffer);

	ftStatus = FT_Write(ftHandle, writeBuffer, (DWORD) buffLength, &dwBytesWritten);

	if (ftStatus == FT_OK) {
		return (jint) dwBytesWritten;
	} else {
		ThrowFTDIException(env, ftStatus, "FT_Write");
		return 0;
	}

}

