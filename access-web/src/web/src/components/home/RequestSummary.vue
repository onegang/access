<template>
  <v-container>
    <h3 class="pt-3">Request Summary</h3>
    <v-divider />
    <div v-if="REQUESTFORM.purpose">Purpose: {{REQUESTFORM.purpose}}</div>
    <div>Effective Date: {{REQUESTFORM.effectiveDate}}</div>
    <div v-if="REQUESTFORM.expiryDate">Expiry Date: {{REQUESTFORM.expiryDate}}</div>
    <div v-if="REQUESTFORM.supporters.length>0">Supporters: {{REQUESTFORM.supporters.join(", ")}}</div>
    <div v-if="REQUESTFORM.comments">Comments: {{REQUESTFORM.comments}}</div>
    <div v-if="REQUESTFORM.attachments.length>0">Supporting Documents: {{REQUESTFORM.attachments.map(file => file.name).join(", ")}}</div>

    <h3 class="pt-10">Access Changes</h3>
    <v-divider class="pb-3" />
    <v-skeleton-loader v-if="REQUEST_CHANGES==null" type="paragraph" />
    <div v-if="!hasChanges">No changes!</div>
    <div v-if="REQUESTFORM.manual">{{REQUESTFORM.manual}}</div>
    <div v-if="REQUEST_CHANGES && REQUEST_CHANGES.added">
      <ChangeItem v-for="(added, index) in REQUEST_CHANGES.added" :key="`add-${index}`" :change="added" type="ADD" />
    </div>
    <div v-if="REQUEST_CHANGES && REQUEST_CHANGES.removed">
      <ChangeItem v-for="(removed, index) in REQUEST_CHANGES.removed" :key="`remove-${index}`" :change="removed" type="REMOVE" />
    </div>

    <div v-if="hasChanges">
      <h3 class="pt-10">Approvals</h3>
      <v-divider class="pb-3" />
      <div v-if="REQUEST_APPROVERS">Approvers: {{REQUEST_APPROVERS.map(user=>user.name).join(", ")}}</div>
      <v-skeleton-loader v-if="REQUEST_APPROVERS==null" type="list-item-avatar-two-line" />
    </div>

    <h3 class="pt-10">Effective User Access</h3>
    <v-divider class="pb-3" />
    <div v-for="user of selectedUsers" v-bind:key="user.name">
        <UserDetails :user="user" readonly />
    </div>
  </v-container>
</template>

<script>
import {mapGetters} from 'vuex';
import UserDetails from '../shared/UserDetails.vue';
import ChangeItem from './ChangeItem.vue';

export default {
  components: {
    UserDetails,
    ChangeItem,
  },
  computed: {
    selectedUsers() {
      return this.USERS.filter(user => user.selected);
    },
    hasChanges() {
      if(this.REQUEST_CHANGES==null)
        return false;
      return this.REQUEST_CHANGES.added.length>0 || this.REQUEST_CHANGES.removed.length>0 || this.REQUESTFORM.manual;
    },
    ...mapGetters(['REQUESTFORM', 'USERS', 'REQUEST_CHANGES', 'REQUEST_APPROVERS']),
  },
};
</script>

<style>
  
</style>
