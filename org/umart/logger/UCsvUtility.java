package org.umart.logger;

import java.util.*;

/**
 * CSV�`���f�[�^���������߂̃��[�e�B���e�B�N���X�ł��D
 * @author ����@��
 */
public class UCsvUtility {

  /**
   * CSV�`���ɏ]����s���̕������v�f�ɕ������C�����̗v�f��ArrayList�Ɋi�[���ĕԂ��D
   * @param ������
   * @return �g�[�N���̔z��
   */
  public static ArrayList split(String line) {
    ArrayList result = new ArrayList();
    StringTokenizer st = new StringTokenizer(line, ",", true);
    String latestToken = "";
    while (true) {
      try {
        String token = st.nextToken();
        latestToken = token;
        if (token.equals(",")) {
          result.add("");
        } else {
          result.add(token);
          token = st.nextToken();
          latestToken = token;
          if (!token.equals(",")) {
            System.err.println("Error: Wrong format in UMemberLog.readLineIntoArray");
            System.exit(5);
          }
        }
      } catch (NoSuchElementException nsee) {
        if (latestToken.equals(",")) {
          result.add("");
        }
        break;
      }
    }
    return result;
  }

}