package io.harness.grpc;

import com.google.inject.Singleton;

import io.grpc.stub.StreamObserver;
import io.harness.delegate.CancelTaskRequest;
import io.harness.delegate.CancelTaskResponse;
import io.harness.delegate.DelegateServiceGrpc.DelegateServiceImplBase;
import io.harness.delegate.SubmitTaskRequest;
import io.harness.delegate.SubmitTaskResponse;
import io.harness.delegate.TaskProgressRequest;
import io.harness.delegate.TaskProgressResponse;
import io.harness.delegate.TaskProgressUpdatesRequest;
import io.harness.delegate.TaskProgressUpdatesResponse;

@Singleton
public class DelegateServiceGrpc extends DelegateServiceImplBase {
  @Override
  public void submitTask(SubmitTaskRequest request, StreamObserver<SubmitTaskResponse> responseObserver) {
    responseObserver.onNext(SubmitTaskResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void cancelTask(CancelTaskRequest request, StreamObserver<CancelTaskResponse> responseObserver) {
    responseObserver.onNext(CancelTaskResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void taskProgress(TaskProgressRequest request, StreamObserver<TaskProgressResponse> responseObserver) {
    responseObserver.onNext(TaskProgressResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void taskProgressUpdates(
      TaskProgressUpdatesRequest request, StreamObserver<TaskProgressUpdatesResponse> responseObserver) {
    /* Some loop should be used here, around onNext, in order to generate a stream of events */
    responseObserver.onNext(TaskProgressUpdatesResponse.newBuilder().build());
    responseObserver.onCompleted();
  }
}
