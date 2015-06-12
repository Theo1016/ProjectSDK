package com.theo.sdk.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.theo.sdk.constant.Const;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/**
 * File������
 * @author Theo
 *
 */
public class FileUtils {

	private String picpath = "calabar/User/";
	// SDcard�ļ�·��;
	private static String SDPATH;

	/**
	 ** ���췽�� ��ȡsd��·��
	 * ***/
	public FileUtils() {
		// ��õ�ǰ�ⲿ�洢�豸��·��
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param bitmap
	 * @param fileName
	 * @throws IOException
	 */
	public File writeSDFromInput(Context mContext, String fileName,
			Bitmap bitmap) {
		if (bitmap == null)
			return null;
		File file = null;
		File tempf;
		try {
			// ����SD���ϵ�Ŀ¼
			tempf = createSDDir(picpath);
			synchronized (tempf) {
				Log.i(Const.LogTag, "directory in the sd card:" + tempf.exists());
				String nameString = picpath + fileName;
				// removeFile(nameString);
				// ��ӱ��
				RenameFile(fileName); 
				file = CreateSDFile(nameString);
				Log.i(Const.LogTag,"file in the sd card:" + file.exists());
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				String name = fileName.substring(fileName.lastIndexOf("."));
				if (name.equals(".jpg")) {
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				} else if (name.equals(".png")) {
					// PNG��ʽ�޷�����ѹ��
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos); 
				}
				bos.flush();
				bos.close();
				// ɾ�������
				DeletemarkFile();
			}

			// removeFile(path+marks);
		} catch (FileNotFoundException e) {
			LogUtils.e(mContext, "writeSDFromInput", e.getMessage(), true);

		} catch (IOException e) {
			LogUtils.e(mContext, "writeSDFromInput", e.getMessage(), true);
		}
		return file;
	}

	/**
	 * ɾ������ļ�
	 */
	public void DeletemarkFile() {
		File f = new File(SDPATH + picpath);
		if (f.exists()) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				File file;
				if (files == null)
					return;
				for (File file1 : files) {
					file = file1;
					String oldname = file.getName();
					if (oldname.contains(".lock")) {
						file.delete();
					}
				}
			}
		}

	}

	/**
	 * ��SD�����洴���ļ�
	 * 
	 * @throws IOException
	 * */
	public File CreateSDFile(String fileName) throws IOException {
		Log.i(Const.LogTag,"filename:" + fileName);
		File file = new File(SDPATH + fileName);
		return file;
	}

	/**
	 * �޸���ǰ��ͼƬΪ����ļ�
	 */
	public void RenameFile(String filename) {
		File f = new File(SDPATH + picpath);
		if (f.exists()) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				if (files == null)
					return;
				for (File file : files) {
					String oldname = file.getName();
					// ֻ�������ͼƬ�����������Ҫlock
					if (oldname.contains(filename)) {
						String newname = oldname.substring(0, oldname.length());
						newname = newname + ".lock";
						if (!oldname.equals(newname)) {
							String path = file.getParent();
							File newfile = new File(path + "/" + newname);
							if (!newfile.exists()) {
								file.renameTo(newfile);
							}
						}
					}

				}
			}
		}

	}

	/**
	 * 
	 * ��SD���ϴ���Ŀ¼ 
	 * 
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.i(Const.LogTag, "the result of making directory:" + dir.mkdirs());
		}
		return dir;
	}
}
