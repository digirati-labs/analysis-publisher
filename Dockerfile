FROM openjdk:11

LABEL com.github.actions.name="Analysis Publisher"
LABEL com.github.actions.description="Publish code analysis results as diff annotations."
LABEL com.github.actions.icon="code"
LABEL com.github.actions.color="yellow"
LABEL maintainer="Gary Tierney <gary.tierney@fastmail.com>"

COPY . /opt/analysis-publisher
WORKDIR /opt/analysis-publisher
RUN ./gradlew --no-daemon installDist

CMD ["--help"]
ENTRYPOINT ["/opt/analysis-publisher/build/install/analysis-publisher/bin/analysis-publisher"]