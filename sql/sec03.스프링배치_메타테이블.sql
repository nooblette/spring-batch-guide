# BATCH_JOB_INSTANCE : Job Parameter(Spring Batch가 실행될때 외부에서 받는 파라미터)에 따라 row가 생성되는 테이블
SELECT  JOB_INSTANCE_ID, # pk
        VERSION,
        JOB_NAME, # 수행한 batch job name
        JOB_KEY
FROM    BATCH_JOB_INSTANCE
;

# BATCH_JOB_EXECUTION : Job Parameter에 따라 실행 결과(실패/성공) row를 저장하는 테이블(BATCH_JOB_INSTANCE의 자식 관계)
SELECT  JOB_EXECUTION_ID, # PK
        VERSION,
        JOB_INSTANCE_ID, # FK (references BATCH_JOB_INSTANCE.JOB_INSTANCE_ID)
        CREATE_TIME,
        START_TIME,
        END_TIME,
        STATUS,
        EXIT_CODE,
        EXIT_MESSAGE,
        LAST_UPDATED
FROM    BATCH_JOB_EXECUTION
;
