#!/bin/bash

# Define EDLab Multiple Computer Parameters
# Need to change before running for TA
EDLAB_FRONTEND_IP=128.119.243.164:5018
EDLAB_CATELOG_IP=128.119.243.175:5019
EDLAB_ORDER_IP=128.119.243.175:5039

# No Need to Change #
EDLAB_BOOK_DATA_URL="../tests/edlab_test_book_data.csv"
EDLAB_FRONTEND_LOG_URL="../tests/edlab_test_frontend_log_file.csv"
EDLAB_CATELOG_LOG_URL="../tests/edlab_test_catelog_log_file.csv"
EDLAB_ORDER_LOG_URL="../tests/edlab_test_order_log_file.csv"
EDLAB_CLIENT_COMMAND_LIST_URL="../tests/edlab_test_client_command_list.csv"
EDLAB_CLIENT_LOG_URL="../tests/edlab_test_client_log_file.csv"

# Define Local Parameters
# No need to change for TA
LOCAL_FRONTEND_IP=localhost:5000
LOCAL_CATELOG_IP=localhost:5100
LOCAL_ORDER_IP=localhost:5200

# Define Docker Parameters
DOCKER_FRONTEND_IP=frontend:5000
DOCKER_CATELOG_IP=catalog:5100
DOCKER_ORDER_IP=order:5200

# No Need to Change #
LOCAL_BOOK_DATA_URL="./tests/local_test_book_data.csv"
LOCAL_FRONTEND_LOG_URL="./tests/local_test_frontend_log_file.csv"
LOCAL_CATELOG_LOG_URL="./tests/local_test_catelog_log_file.csv"
LOCAL_ORDER_LOG_URL="./tests/local_test_order_log_file.csv"
LOCAL_CLIENT_COMMAND_LIST_URL="./tests/local_test_client_command_list.csv"
LOCAL_CLIENT_LOG_URL="./tests/local_test_client_log_file.csv"

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

elif [ $1 == 'docker' ] && [ $2 == 'frontend' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.FrontEndService" -Dexec.args="$DOCKER_FRONTEND_IP $DOCKER_CATELOG_IP $DOCKER_ORDER_IP $LOCAL_FRONTEND_LOG_URL"
elif [ $1 == 'docker' ] && [ $2 == 'catelog' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.CatelogService" -Dexec.args="$DOCKER_FRONTEND_IP $DOCKER_CATELOG_IP $DOCKER_ORDER_IP $LOCAL_BOOK_DATA_URL $LOCAL_CATELOG_LOG_URL"
elif [ $1 == 'docker' ] && [ $2 == 'order' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.OrderService" -Dexec.args="$DOCKER_FRONTEND_IP $DOCKER_CATELOG_IP $DOCKER_ORDER_IP $LOCAL_ORDER_LOG_URL"
elif [ $1 == 'docker' ] && [ $2 == 'client' ]
then
    mvn exec:java -Dexec.mainClass="com.dslab2.Client" -Dexec.args="$DOCKER_FRONTEND_IP $LOCAL_CLIENT_COMMAND_LIST_URL $LOCAL_CLIENT_LOG_URL"

elif [ $1 == 'edlab' ] && [ $2 == 'frontend' ]
then
    cd ./target/
    java -cp com.dslab2-1.0-jar-with-dependencies.jar com.dslab2.FrontEndService $EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLAB_FRONTEND_LOG_URL
    #mvn exec:java -Dexec.mainClass="com.dslab2.FrontEndService" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLAB_FRONTEND_LOG_URL"
elif [ $1 == 'edlab' ] && [ $2 == 'catelog' ]
then
    cd ./target/
    java -cp com.dslab2-1.0-jar-with-dependencies.jar com.dslab2.CatelogService $EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLAB_BOOK_DATA_URL $EDLAB_CATELOG_LOG_URL
    #mvn exec:java -Dexec.mainClass="com.dslab2.CatelogService" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLAB_BOOK_DATA_URL $EDLAB_CATELOG_LOG_URL"
elif [ $1 == 'edlab' ] && [ $2 == 'order' ]
then
    cd ./target/
    java -cp com.dslab2-1.0-jar-with-dependencies.jar com.dslab2.OrderService $EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLAB_ORDER_LOG_URL
    #mvn exec:java -Dexec.mainClass="com.dslab2.OrderService" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CATELOG_IP $EDLAB_ORDER_IP $EDLABORDER_LOG_URL"
elif [ $1 == 'edlab' ] && [ $2 == 'client' ]
then
    cd ./target/
    java -cp com.dslab2-1.0-jar-with-dependencies.jar com.dslab2.Client $EDLAB_FRONTEND_IP $EDLAB_CLIENT_COMMAND_LIST_URL $EDLAB_CLIENT_LOG_URL
    #mvn exec:java -Dexec.mainClass="com.dslab2.Client" -Dexec.args="$EDLAB_FRONTEND_IP $EDLAB_CLIENT_COMMAND_LIST_URL $EDLAB_CLIENT_LOG_URL"
fi