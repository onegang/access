<template>
  <v-container>
    <h3 class="pt-3">Request Summary</h3>
    <v-divider />
    <div>Effective Date: {{REQUESTFORM.effectiveDate}}</div>
    <div v-if="REQUESTFORM.expiryDate">Expiry Date: {{REQUESTFORM.expiryDate}}</div>
    <div v-if="REQUESTFORM.comments">Comments: {{REQUESTFORM.comments}}</div>
    <div v-if="REQUESTFORM.attachments.length>0">Supporting Documents: {{REQUESTFORM.attachments.map(file => file.name).join(", ")}}</div>

    <h3 class="pt-10">Access Changes</h3>
    <v-divider class="pb-3" />
    <v-skeleton-loader type="paragraph" />

    <h3 class="pt-10">Approvals</h3>
    <v-divider class="pb-3" />
    <v-skeleton-loader type="list-item-avatar-two-line" />

    <h3 class="pt-10">Effective User Access</h3>
    <v-divider class="pb-3" />
    <div v-for="user of selectedUsers" v-bind:key="user.name">
        <UserDetails v-bind:user="user" readonly />
    </div>
  </v-container>
</template>

<script>
import {mapGetters} from 'vuex';
import UserDetails from './UserDetails.vue';

export default {
  components: {
    UserDetails,
  },
  computed: {
    selectedUsers() {
      return this.USERS.filter(user => user.selected);
    },
    ...mapGetters(['REQUESTFORM', 'USERS']),
  },
};
</script>

<style>
  
</style>
