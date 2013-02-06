package org.wmart.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.wmart.agent.*;

/**
 * WBaseAgent�̔h���N���X�𐶐����邽�߂̃t�@�N�g���N���X�ł��D
 * 
 * @author ����@��
 * @author Ikki Fujiwara, NII
 * 
 */
@SuppressWarnings("finally")
public class WAgentFactory {
	/**
	 * WBaseAgent�̔h���N���X�𐶐�����D
	 * 
	 * @param userName
	 *            ���[�U�[��
	 * @param passwd
	 *            �p�X���[�h
	 * @param className
	 *            �N���X��
	 * @param paramsStr
	 *            �G�[�W�F���g�̃V�X�e���p�����[�^��\��������
	 * @param seed
	 *            �����̎�
	 * @return WBaseAgent�̔h���N���X�̃I�u�W�F�N�g
	 */
	private static WBaseAgent makeNewTypeAgent(String userName, String passwd, String className, String paramsStr,
		int seed, OutputStream os) {
		WBaseAgent result = null;
		try {
			Class targetClass = Class.forName(className);
			Class[] paramTypes = new Class[4];
			paramTypes[0] = userName.getClass();
			paramTypes[1] = passwd.getClass();
			paramTypes[2] = className.getClass();
			paramTypes[3] = Integer.TYPE;
			Constructor constructor = targetClass.getConstructor(paramTypes);
			Object[] args = new Object[4];
			args[0] = userName;
			args[1] = passwd;
			args[2] = className;
			args[3] = new Integer(seed);
			result = (WBaseAgent) constructor.newInstance(args);
			result.setOutputStream(os);
			String[] params = new String[0];
			if (paramsStr != null) {
				StringTokenizer st = new StringTokenizer(paramsStr, ":");
				params = new String[st.countTokens()];
				for (int i = 0; i < params.length; ++i) {
					params[i] = st.nextToken();
				}
			}
			result.setParameters(params);
		} catch (NoSuchMethodException nme) {
			result = null;
		} finally {
			return result;
		}
	}

	/**
	 * WBaseAgent�̔h���N���X�𐶐�����D
	 * 
	 * @param userName
	 *            ���[�U�[��
	 * @param passwd
	 *            �p�X���[�h
	 * @param �N���X��
	 * @param paramsStr
	 *            �G�[�W�F���g�̃V�X�e���p�����[�^��\��������
	 * @param seed
	 *            �����̎�
	 * @param os
	 *            �o�̓X�g���[��
	 * @return WBaseAgent�̔h���N���X�̃I�u�W�F�N�g
	 */
	public static WBaseAgent makeAgent(String userName, String passwd, String className, String paramsStr, int seed,
		OutputStream os) throws IllegalArgumentException {
		WBaseAgent result = makeNewTypeAgent(userName, passwd, className, paramsStr, seed, os);
		if (result == null) {
			throw new IllegalArgumentException();
		}
		return result;
	}

	/**
	 * WBaseAgent�̔h���N���X�𐶐�����D
	 * 
	 * @param userName
	 *            ���[�U��
	 * @param passwd
	 *            �p�X���[�h
	 * @param className
	 *            �N���X��
	 * @param paramsStr
	 *            �G�[�W�F���g�̃V�X�e���p�����[�^��\��������
	 * @param seed
	 *            �����̎�
	 * @return WBaseAgent�̔h���N���X�̃I�u�W�F�N�g
	 * @throws IllegalArgumentException
	 */
	public static WBaseAgent makeAgent(String userName, String passwd, String className, String paramsStr, int seed)
		throws IllegalArgumentException {
		return makeAgent(userName, passwd, className, paramsStr, seed, System.out);
	}

}