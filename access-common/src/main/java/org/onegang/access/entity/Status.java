package org.onegang.access.entity;

/**
 * Workflow of status:
 * New -> Approving --> Approved -> Implementing (on effective date) -> Done
 *                  +-> Rejected
 *                  +-> Cancelled 
 * 
 * @author TC
 *
 */
public enum Status {

	APPROVING, IMPLEMENTING, DONE, CANCELLED, //request status
	PENDING, APPROVED, REJECTED //approval status
}
