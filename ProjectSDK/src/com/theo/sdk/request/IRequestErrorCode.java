package com.theo.sdk.request;

/**
 * �ͻ����ڲ������ʾ
 * @author Theo
 *
 */
public interface IRequestErrorCode {    

    /** δ֪���� */
    int ERROR_CODE_UNKNOW = -1;
    
    /** û�������Url��ַ */
    int ERROR_CODE_NO_URL = ERROR_CODE_UNKNOW - 1;
    
    /** ������ʴ��� */
    int ERROR_CODE_NET_FAILED = ERROR_CODE_NO_URL - 1;
    
    /** ��ȡ����String����Json��ʽ */
    int ERROR_CODE_RESULT_IS_NOT_JSON_STYLE = ERROR_CODE_NET_FAILED - 1;
    
    /** ���ݽ������� */
    int ERROR_CODE_PARSE_DATA_ERROR = ERROR_CODE_RESULT_IS_NOT_JSON_STYLE - 1;
    /** �������·��˿�����*/
    int ERROR_CODE_DATA_VACANCY = ERROR_CODE_PARSE_DATA_ERROR - 1;
    
}
