package org.umart.serverCore;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.umart.strategyCore.UBaseAgent;


/**
 * UBaseAgent�̔h���N���X�𐶐����邽�߂̃t�@�N�g���N���X�ł��D
 * U-Mart Version 1��Strategy�̔h���N���X�������I�ɔ��ʂ��Đ������܂��D
 * @author ����@��
 *
 */
public class UAgentFactory {
  /**
   * U-Mart Version 1�ɂ�����Strategy�̔h���N���X�𐶐�����D
   * @param userName ���O�C����
   * @param passwd �p�X���[�h
   * @param className �N���X��
   * @param paramsStr �G�[�W�F���g�̃V�X�e���p�����[�^��\��������
   * @param seed �����̎�
   * @return UBaseStrategy�̔h���N���X�̃I�u�W�F�N�g
   */
  private static UBaseAgent makeOldTypeStrategy(String loginName,
      String passwd,
      String className, String paramsStr, int seed,
      OutputStream os) {
    UBaseAgent result = null;
    try {
      Class targetClass = Class.forName(className);
      Class[] paramTypes = new Class[1];
      paramTypes[0] = Integer.TYPE;
      Constructor constructor = targetClass.getConstructor(paramTypes);
      Object[] args = new Object[1];
      args[0] = new Integer(seed);
      result = (UBaseAgent) constructor.newInstance(args);
      result.setLoginName(loginName);
      result.setPasswd(passwd);
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
      result.setRealName(className);
    } catch (NoSuchMethodException nme) {
      result = null;
    } finally {
      return result;
    }
  }

  /**
   * UBaseAgent�̔h���N���X�𐶐�����D
   * @param userName ���[�U�[��
   * @param passwd �p�X���[�h
   * @param className �N���X��
   * @param paramsStr �G�[�W�F���g�̃V�X�e���p�����[�^��\��������
   * @param seed �����̎�
   * @return UBaseAgent�̔h���N���X�̃I�u�W�F�N�g
   */
  private static UBaseAgent makeNewTypeAgent(String userName, String passwd, String className, String paramsStr, int seed, OutputStream os) {
    UBaseAgent result = null;
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
      result = (UBaseAgent) constructor.newInstance(args);
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
   * UBaseAgent�̔h���N���X�𐶐�����D
   * ���̃��\�b�h�͋��o�[�W�����ɂ�����Strategy�N���X�̔h���N���X�̃I�u�W�F�N�g�������\�ł���D
   * @param userName ���[�U�[��
   * @param passwd �p�X���[�h
   * @param �N���X��
   * @param paramsStr �G�[�W�F���g�̃V�X�e���p�����[�^��\��������
   * @param seed �����̎�
   * @param os �o�̓X�g���[��
   * @return UBaseAgent�̔h���N���X�̃I�u�W�F�N�g
   */
  public static UBaseAgent makeAgent(String userName, String passwd,
                                       String className, String paramsStr,
                                       int seed, OutputStream os) throws IllegalArgumentException {
    UBaseAgent result = makeNewTypeAgent(userName, passwd, className,
                                         paramsStr, seed, os);
    if (result == null) {
      result = makeOldTypeStrategy(userName, passwd, className, paramsStr, seed, os);
      if (result == null) {
        throw new IllegalArgumentException();
      }
    }
    return result;
  }

  /**
   * UBaseAgent�̔h���N���X�𐶐�����D
   * @param userName ���[�U��
   * @param passwd �p�X���[�h
   * @param className �N���X��
   * @param paramsStr �G�[�W�F���g�̃V�X�e���p�����[�^��\��������
   * @param seed �����̎�
   * @return UBaseAgent�̔h���N���X�̃I�u�W�F�N�g
   * @throws IllegalArgumentException
   */
  public static UBaseAgent makeAgent(String userName, String passwd,
                                       String className, String paramsStr,
                                       int seed) throws IllegalArgumentException {
    return makeAgent(userName, passwd, className, paramsStr, seed, System.out);
  }

}