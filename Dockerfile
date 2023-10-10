FROM ruby:2.7.2
# Fetch them from jenkins

COPY Gemfile* /cnvrg_aut/
WORKDIR /cnvrg_aut
RUN gem install bundler --pre
RUN bundle install
COPY . .
CMD ["ruby", "your_ruby_script.rb"]
