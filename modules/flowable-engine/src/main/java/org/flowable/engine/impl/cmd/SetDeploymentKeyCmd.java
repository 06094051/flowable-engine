/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.impl.cmd;

import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.api.FlowableObjectNotFoundException;
import org.flowable.engine.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.impl.FlowableEventBuilder;
import org.flowable.engine.impl.interceptor.Command;
import org.flowable.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.repository.Deployment;

/**
 * @author Tijs Rademakers
 */
public class SetDeploymentKeyCmd implements Command<Void> {

  protected String deploymentId;
  protected String key;

  public SetDeploymentKeyCmd(String deploymentId, String key) {
    this.deploymentId = deploymentId;
    this.key = key;
  }

  public Void execute(CommandContext commandContext) {

    if (deploymentId == null) {
      throw new FlowableIllegalArgumentException("Deployment id is null");
    }

    DeploymentEntity deployment = commandContext.getDeploymentEntityManager().findById(deploymentId);

    if (deployment == null) {
      throw new FlowableObjectNotFoundException("No deployment found for id = '" + deploymentId + "'", Deployment.class);
    }
    
    if (commandContext.getProcessEngineConfiguration().isFlowable5CompatibilityEnabled() && 
        commandContext.getProcessEngineConfiguration().getFlowable5CompatibilityHandler().isVersion5Tag(deployment.getEngineVersion())) {
      return null;
    }

    // Update category
    deployment.setKey(key);

    if (commandContext.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
      commandContext.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_UPDATED, deployment));
    }

    return null;
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public void setDeploymentId(String deploymentId) {
    this.deploymentId = deploymentId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

}