# BATCH_JOB_INSTANCE
# Job Parameter를 외부에서 받고 Spring Batch가 실행에 따라 row가 생성되는 테이블
# Job을 실행하면 가장 먼저 이 테이블에 job instance 정보가 등록된다.
# Job instance 레코드는 job의 논리적 실행을 나타낸다.
# 수행한 batch job name과 job parameter를 기반으로 JOB_KEY를 저장한다.
SELECT  JOB_INSTANCE_ID, # pk, JobInstance의 getId 메서드를 통해 얻을 수 있다.
        VERSION, # Optimistic locking(version을 체크하며 Transaction에 적용되는 동시성 제어 방식(낙관적 락))에 사용되는 version
        JOB_NAME, # 수행한 batch job name
        JOB_KEY # job name과 job parameter를 기반으로 생성
FROM    BATCH_JOB_INSTANCE
where   1 = 1
and     JOB_INSTANCE_ID >= 223
order by JOB_INSTANCE_ID desc
;

# BATCH_JOB_EXECUTION
# Job Parameter에 따라 실행 결과(실패/성공) row를 저장하는 테이블(BATCH_JOB_INSTANCE의 자식 관계)
# Batch Job의 실제 실행(Execution) 기록을 저장
# job이 실행될떄마라 새로운 레코드가 생성되며 job이 진행되는 동안 주기적으로 STATUS(Job 실행 상태), EXIT_CODE(job 작업 결과), LAST_UPDATED 등 컬럼값이 업데이트 된다.
SELECT  JOB_EXECUTION_ID, # PK
        VERSION, # Optimistic locking에 사용
        JOB_INSTANCE_ID, # FK (references BATCH_JOB_INSTANCE.JOB_INSTANCE_ID), BATCH_JOB_INSTANCE 테이블과 1:n 관계를 갖는다.
        CREATE_TIME, # 레코드 생성 시간
        START_TIME, # Job 실행 시작 시간
        END_TIME, # Job 실행 완료 시간(작업이 현재 실행중이지 않는데 이 컬럼값이 null일 경우 오류가 발생해서 실패 전에 마지막 저장을 수행하지 못한것으로 판단할 수 있다)
        STATUS, # batch job 실행 상태, BatchStatus.enum 값 중 하나가 들어간다.
        EXIT_CODE, # job 실행 결과(String, ExitStatus.java 클래스를 참고한다.)
        EXIT_MESSAGE, # EXIT_CODE와 관련된 예외 메시지 또는 stack trace
        LAST_UPDATED # 레코드 마지막 갱신 시간
FROM    BATCH_JOB_EXECUTION
WHERE   1 = 1
AND     JOB_EXECUTION_ID >= 246;
;

# BATCH_JOB_EXECUTION_PARAMS
# BATCH_JOB_EXECUTION 테이블에 row가 생성될 당시 입력받은 job parameter 정보 보관
# job이 실행될때 입력받은 job parameter가 없다면 레코드는 0개, 있다면 그 개수만큼 key/value 페어로 생성된다.
# 스프링 5.0 이전 버전과 스키마가 다름(이전 버전일 경우 타입에 따라 STRING_VAL, DATE_VAL, LONG_VAL 등으로 반정규화)
SELECT  JOB_EXECUTION_ID, # FK (references BATCH_JOB_EXECUTION.JOB_EXECUTION_ID)
        PARAMETER_NAME, # job parameter name
        PARAMETER_TYPE, # job parameter type
        PARAMETER_VALUE, # job parameter value
        IDENTIFYING # # ob instance 생성시 hash 값에 참여했는지 여부
FROM    BATCH_JOB_EXECUTION_PARAMS
WHERE   JOB_EXECUTION_ID >= 246
;

# BATCH_JOB_EXECUTION_CONTEXT
# Job Execution Context에 대한 정보 저장
# JobExecution 내에서 사용되며, 각 Step에서 접근하면서 공유할 수 있다.
select  JOB_EXECUTION_ID, # BATCH_JOB_EXECUTION의 FK
        SHORT_CONTEXT, # SERIALIZER_CONTEXT의 String Version
        SERIALIZED_CONTEXT # 직렬화된 ExecutionContext
from    BATCH_JOB_EXECUTION_CONTEXT
where   1 = 1
and     JOB_EXECUTION_ID >= 235
;

# BATCH_STEP_EXECUTION
# step과 관련된 정보(STEP_EXECUTION_ID, STEP_NAME 등), Step의 실행 상태(STATUS), Step의 작업 결과(EXIT_CODE) 등을 저장한다.
# StepExecution 내에서 사용되며 각 Step끼리 서로의 Step Execution을 접근할 수 없다.
select  STEP_EXECUTION_ID, # PK, stepExecution 객체의 getId 메서드로 얻을 수 있다.
        VERSION, # Optimistic locking에 사용
        STEP_NAME, # step 이름
        JOB_EXECUTION_ID, # BATCH_JOB_EXECUTION의 FK
        CREATE_TIME, # step 생성 시간
        START_TIME, # step 실행 시작 시간
        END_TIME, # step 실행 완료 시간, 작업이 현재 실행중이지 않는데 이 값이 비어있는 경우 오류가 발생해서 마지막 저장을 수행하지 못한걸로 판단한다.
        STATUS, # Step 실행 상태, BATCH_JOB_EXECUTION과 동일하게 BatchStatus.enum 값 중 하나가 들어간다.
        COMMIT_COUNT, # Step 실행 동안 커밋된 트랜잭션 수
        READ_COUNT, # Step 실행동안 읽은 item 수
        FILTER_COUNT, # Step 실행동안 itemProcessor가 null을 반환해 필터링된 아이템 수
        WRITE_COUNT, # Step 실행동안 기록한 Item 수
        READ_SKIP_COUNT, # ItemReader 내에서 예외가 던져졌을때 건너뛴 아이템 수
        WRITE_SKIP_COUNT, # ItemWriter 내에서 예외가 던져졌을때 건너뛴 아이템 수
        PROCESS_SKIP_COUNT, # ItemProcessor 내에서 예외가 던져졌을때 건너뛴 아이템 수
        ROLLBACK_COUNT, # Step에서 rollback된 트랜잭션 수
        EXIT_CODE, # Step의 작업 결과(String, ExitStatus.java 클래스를 참고한다.)
        EXIT_MESSAGE, # EXIT_CODE와 관련된 예외 메시지 또는 stack trace
        LAST_UPDATED # 레코드 마지막 갱신 시간
from    BATCH_STEP_EXECUTION
where   1 = 1
and     JOB_EXECUTION_ID >= 246
;

# BATCH_STEP_EXECUTION_CONTEXT
# Step ExecutionContext, StepExecution 내에서 사용되며, 각 Step끼리 공유할 수 없다.
select  STEP_EXECUTION_ID, # BATCH_STEP_EXECUTION의 FK
        SHORT_CONTEXT, # SERIALIZER_CONTEXT의 String Version
        SERIALIZED_CONTEXT # 직렬화된 ExecutionContext
from    BATCH_STEP_EXECUTION_CONTEXT
where   1 = 1
and     STEP_EXECUTION_ID >= 315
;


# 시퀀스 테이블
select  ID, UNIQUE_KEY
from    BATCH_JOB_SEQ
;
select  ID, UNIQUE_KEY
from    BATCH_JOB_EXECUTION_SEQ
;
select  ID, UNIQUE_KEY
from    BATCH_STEP_EXECUTION_SEQ
;
