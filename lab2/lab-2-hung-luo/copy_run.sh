#!/bin/bash

# Define EDLab Multiple Computer Parameters
# Need to change before running for TA
EDLAB_FRONTEND_IP=elnux2.cs.umass.edu:5001
EDLAB_CATELOG_IP=elnux7.cs.umass.edu:5001
EDLAB_ORDER_IP=elnux7.cs.umass.edu:5002

# No Need to Change #
EDLAB_BOOK_DATA_URL="./tests/edlab_test_book_data.csv"
EDLAB_FRONTEND_LOG_URL="./tests/edlab_test_frontend_log_file.csv"
EDLAB_CATELOG_LOG_URL="./tests/edlab_test_catelog_log_file.csv"
EDLAB_ORDER_LOG_URL="./tests/edlab_test_order_log_file.csv"
EDLAB_CLIENT_COMMAND_LIST_URL="./tests/edlab_test_client_command_list.csv"
EDLAB_CLIENT_LOG_URL="./tests/edlab_test_client_log_file.csv"

# Define Local Parameters
# No need to change for TA
LOCAL_FRONTEND_IP=localhost:5000
LOCAL_CATELOG_IP=localhost:5100
LOCAL_ORDER_IP=localhost:5200

# No Need to Change #
LOCAL_BOOK_DATA_URL="./tests/local_test_book_data.csv"
LOCAL_FRONTEND_LOG_URL="./tests/local_test_frontend_log_file.csv"
LOCAL_CATELOG_LOG_URL="./tests/local_test_catelog_log_file.csv"
LOCAL_ORDER_LOG_URL="./tests/local_test_order_log_file.csv"
LOCAL_CLIENT_COMMAND_LIST_URL="./tests/local_test_client_command_list.csv"
LOCAL_CLIENT_LOG_URL="./tests/local_test_client_log_file.csv"

# Compile
mvn verify

if [ $1 == 'local' ] && [ $2 == 'frontend' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.FrontEndService" -Dexec.args="$LOCAL_FRONTEND_IP $LOCAL_CATELOG_IP $LOCAL_ORDER_IP $LOCAL_FRONTEND_LOG_URL"
elif [ $1 == 'local' ] && [ $2 == 'catelog' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.CatelogService" -Dexec.args="$LOCAL_FRONTEND_IP $LOCAL_CATELOG_IP $LOCAL_ORDER_IP $LOCAL_BOOK_DATA_URL $LOCAL_CATELOG_LOG_URL"
elif [ $1 == 'local' ] && [ $2 == 'order' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.OrderService" -Dexec.args="$LOCAL_FRONTEND_IP $LOCAL_CATELOG_IP $LOCAL_ORDER_IP $LOCAL_ORDER_LOG_URL"
elif [ $1 == 'local' ] && [ $2 == 'client' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.Client" -Dexec.args="$LOCAL_FRONTEND_IP $LOCAL_CLIENT_COMMAND_LIST_URL $LOCAL_CLIENT_LOG_URL"

elif [ $1 == 'edlab' ] && [ $2 == 'frontend' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.FrontEndService" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLAB_FRONTEND_LOG_URL"
elif [ $1 == 'edlab' ] && [ $2 == 'catelog' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.CatelogService" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLAB_BOOK_DATA_URL $EDLAB_CATELOG_LOG_URL"
elif [ $1 == 'edlab' ] && [ $2 == 'order' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.OrderService" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLABORDER_LOG_URL"
elif [ $1 == 'edlab' ] && [ $2 == 'client' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.Client" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CLIENT_COMMAND_LIST_URL $EDLAB_CLIENT_LOG_URL"

fi