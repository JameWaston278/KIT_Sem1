package ai;

import java.util.List;

import model.Position;

/**
 * The AIStepListener interface defines a callback method that is invoked when
 * an AI step is executed. It allows implementing classes to receive logs and
 * the target position of the AI's action. This can be used for logging,
 * debugging, or updating the game state based on the AI's decisions.
 * 
 * @author udqch
 */
@FunctionalInterface
public interface AIStepListener {

    /**
     * This method is called when an AI step is executed. It provides the logs
     * generated during the step and the target position of the AI's action.
     *
     * @param logs      A list of log messages generated during the AI step
     *                  execution.
     * @param targetPos The target position resulting from the AI's action.
     */
    void onStepExecuted(List<String> logs, Position targetPos);
}