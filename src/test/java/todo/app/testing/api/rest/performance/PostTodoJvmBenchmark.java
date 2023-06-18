package todo.app.testing.api.rest.performance;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration;
import todo.app.testing.api.rest.config.TodoRestClientConfigData;
import todo.app.testing.api.rest.config.TodoRestClientConfiguration;
import todo.app.testing.api.rest.dto.TodoEntity;

@Slf4j
@SuppressWarnings("NewClassNamingConvention")
@State(Scope.Benchmark)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("performance")
@SpringBootTest(classes = {TodoRestClientConfiguration.class, TodoAppInDockerConfiguration.class})
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 10, time = 10)
@BenchmarkMode({Mode.SingleShotTime, Mode.Throughput})
public class PostTodoJvmBenchmark {

    @Test
    public void executeJmhRunner() throws RunnerException {
        var opt = new OptionsBuilder()
                .include(PostTodoJvmBenchmark.class.getSimpleName())
                // do not use forking or the benchmark methods will not see references stored within its class
                // do not use multiple threads with Spring because 1 bean with application container
                .forks(0)
                .threads(1)
                .shouldDoGC(true)
                .verbosity(VerboseMode.EXTRA)
                .resultFormat(ResultFormatType.CSV)
                .result("jmh-report.csv")
                .shouldFailOnError(true)
                .jvmArgs("-server")
                .mode(Mode.Throughput)
                .build();
        new Runner(opt).run();
    }

    private static RestOperations restOperations;
    private static AtomicLong idGenerator;
    private static TodoRestClientConfigData settings;
    private static HttpHeaders httpHeaders;

    @Autowired
    void setRestOperations(RestOperations restOperations) {
        PostTodoJvmBenchmark.restOperations = restOperations;
    }

    @Autowired
    void setHttpSettings(TodoRestClientConfigData settings) {
        PostTodoJvmBenchmark.settings = settings;
    }

    @Setup(Level.Trial)
    public void setup() {
        idGenerator = new AtomicLong(0);
        httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Benchmark
    public void someBenchmarkMethod(Blackhole blackhole) {
        var todoEntity = new TodoEntity(idGenerator.incrementAndGet(), "test", false);
        var todoEntityHttpEntity = new HttpEntity<>(todoEntity, httpHeaders);
        blackhole.consume(restOperations.exchange(
                settings.getTodosPostPath(), HttpMethod.POST, new HttpEntity<>(todoEntity, httpHeaders), String.class));
        blackhole.consume(todoEntity);
        blackhole.consume(todoEntityHttpEntity);
    }
}
