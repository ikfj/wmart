package org.wmart.cmdCore;

import java.util.*;

/**
 * �R�}���h���`���邽�߂̃C���^�[�t�F�[�X�ł��D
 * �V�X�e�����̑S�ẴR�}���h�͂��̃C���^�[�t�F�[�X���������Ȃ���΂Ȃ�܂���D
 * @author ����@��
 */
public interface ICommand {

  /** �������Ԉ���Ă��邱�Ƃ�\���萔 */
  public static final int INVALID_ARGUMENTS = 1;

  /** ��t���Ȃ��R�}���h�ł��邱�Ƃ�\���萔 */
  public static final int UNACCEPTABLE_COMMAND = 2;

  /** ���݂��Ȃ��R�}���h�ł��邱�Ƃ�\���萔 */
  public static final int INVALID_COMMAND = 3;

  /**
   * �R�}���h����Ԃ��D
   * @param �R�}���h��
   */
  public String getName();

  /**
   * ���̃R�}���h�̖��O��name�Ɠ��������H
   * @param name ��r����R�}���h��
   * @return true:�������Cfalse:�������Ȃ�
   */
  public boolean isNameEqualTo(String name);

  /**
   * ���̃R�}���h�ɕK�v�Ȉ�����ݒ肷��D
   * @param st �����Q�iStringTokenizer.nextToken�Ŏ��o����j
   * return true�F�����𐳂����ݒ�ł����ꍇ�Cfalse:���s�����ꍇ
   */
  public boolean setArguments(StringTokenizer st);

  /**
   * �R�}���h�����s����D
   * @return ���s����
   */
  public WCommandStatus doIt();

  /**
   * �R�}���h�̎��s���ʂ�W���o�͂ɕ\������D
   */
  public void printOn();

  /**
   * �R�}���h�̎��s���ʂ��X�y�[�X��؂�̕�����Ƃ��ĕԂ��D
   * @return ���s���ʂ̕�����
   */
  public String getResultString();

}
