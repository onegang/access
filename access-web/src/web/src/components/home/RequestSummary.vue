<template>
  <v-container>
    <h3>Request Summary</h3>
    <div>Effective Date: {{REQUESTFORM.effectiveDate}}</div>
    <div v-if="REQUESTFORM.expiryDate">Expiry Date: {{REQUESTFORM.expiryDate}}</div>
    <div v-if="REQUESTFORM.comments">Comments: {{REQUESTFORM.comments}}</div>
    <div v-if="REQUESTFORM.attachments.length>0">Supporting Documents: {{REQUESTFORM.attachments.map(file => file.name).join(", ")}}</div>
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

<style scoped>
  
</style>
