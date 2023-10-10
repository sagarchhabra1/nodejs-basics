FROM ruby:2.7.2
# Fetch them from jenkins
# RUN apt-get update
# RUN apt-get upgrade -y
RUN mkdir -p /cnvrg_aut
COPY Gemfile* /cnvrg_aut/
WORKDIR /cnvrg_aut
RUN gem uninstall bundle
RUN gem install bundler --pre
RUN bundle install
ARG RUN_ID
ARG RERUN
ARG REMOTE_URL
ARG DEFAULT_URL
ARG NEW_TESTRUN_TITLE
ARG PARALLEL_SPLIT_TEST_PROCESSES
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ARG RUN_STATUS
ARG TENANT_USER_NAME
ARG TENANT_EMAIL
ARG TENANT_ORG_NAME
ARG TENANT_URL
ARG UITENANT
ARG TENANT_IMAGE
ARG BUILD_URL
ARG ZALENIUM
ARG SCENARIOS
ARG CUSTOM_RESOURCE_NAME
ARG STORAGE_TYPE


ENV STORAGE_TYPE=${STORAGE_TYPE:-'default'}
ENV CUSTOM_RESOURCE_NAME=${CUSTOM_RESOURCE_NAME:-''}
ENV BUILD_URL=${BUILD_URL:-''}
ENV SCENARIOS=${SCENARIOS:-'default'}
ENV USER=${USER:-''}
ENV TENANT_IMAGE=${TENANT_IMAGE:-''}
ENV ORG=${ORG:-''}
ENV ONE_CLICK=${ONE_CLICK:-'false'}
ENV SSL=${SSL:-'false'}
ENV TENANT_URL=${TENANT_URL}
ENV TENANT_EMAIL=${TENANT_EMAIL:-''}
ENV TENANT_ORG_NAME=${TENANT_ORG_NAME:-''}
ENV TENANT_USER_NAME=${TENANT_USER_NAME:-''}
ENV RUN_ID=${RUN_ID:-3414}
ENV RERUN=${RERUN:-false}
ENV ZALENIUM=${ZALENIUM:-'true'}
RUN apt-get update
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
RUN chmod +x ./kubectl
RUN mv ./kubectl /usr/local/bin
ENV UITENANT=${UITENANT:-'true'}
ENV DEFAULT_URL=${DEFAULT_URL:-''}
ENV REMOTE_URL=${REMOTE_URL:-'http://testgrid.cnvrg.io/wd/hub'}
ENV NEW_TESTRUN_TITLE=${NEW_TESTRUN_TITLE:-'testrun'}
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
ENV RUN_STATUS=${RUN_STATUS}
ENV AWS_REGION='us-east-2'
ENV PARALLEL_SPLIT_TEST_PROCESSES=${PARALLEL_SPLIT_TEST_PROCESSES:-'2'}
ENV SINGLESIGNUP="true"
ENV POPULATE_TEST_RAIL="true"
ENV SELENOID="true"
ENV EXCLUDE_CASES='{"not_on_aks": ["23182"], "not_on_eks": [], "not_on_gke": [] ,"only_master":["29736"]}'
ENV TEST_RAIL_REPORT_ID=4
ENV TAKE_SCREENSHOT="true"
ENV CLUSTER_NAME=false
COPY . .
RUN echo $(printenv)

RUN  PARALLEL_SPLIT_TEST_PROCESSES=1 RUN_STATUS='Passed,Retest,Failed,Warning,Untested,Blocked' PARALLELFILE=createtenant bundle exec ruby parallel_rspec.rb parallel_split_test specs/27_create_tenant_spec.rb --format html --out "report/report.html"
RUN chmod +x final_command.sh
CMD ["/cnvrg_aut/final_command.sh"]
