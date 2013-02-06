package org.umart.serverCore;

/**
 * Read-Write Lock�p�^�[�������������N���X�ł��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public final class UReadWriteLock {

  /** ����, �ǂݏo�����s���Ă���Reader�X���b�h�̐� */
  private int fReadingReaders = 0;

  /** ����, �ҋ@����Writer�X���b�h�̐� */
  private int fWaitingWriters = 0;

  /** ����, �������ݒ��̃X���b�h�̐� */
  private int fWritingWriters = 0;

  /** �������ݗD�悩? */
  private boolean fPreferWriter = true;

  /**
   * Read Lock���擾����B
   */
  public synchronized void readLock() throws InterruptedException {
    while (fWritingWriters > 0
           || (fPreferWriter && fWaitingWriters > 0)) {
      wait();
    }
    ++fReadingReaders;
  }

  /**
   * Read Lock���������B
   */
  public synchronized void readUnlock() {
    --fReadingReaders;
    fPreferWriter = true;
    notifyAll();
  }

  /**
   * Write Lock���擾����B
   */
  public synchronized void writeLock() throws InterruptedException {
    ++fWaitingWriters;
    try {
      while (fReadingReaders > 0 || fWritingWriters > 0) {
        wait();
      }
    } finally {
      --fWaitingWriters;
    }
    ++fWritingWriters;
  }

  /**
   * Write Lock���������B
   */
  public synchronized void writeUnlock() {
    --fWritingWriters;
    fPreferWriter = false;
    notifyAll();
  }
}
