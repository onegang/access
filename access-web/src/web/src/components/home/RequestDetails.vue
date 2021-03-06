<template>
  <v-form v-model="valid">
    <v-container>
      <v-row>
        <v-col cols="12" md="12">
          <v-text-field
            v-model="purpose"
            label="Purpose"
            clearable
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row> 
        <v-col cols="12" sm="6" md="4">
          <v-menu
            v-model="effectiveDateMenu"
            :close-on-content-click="false"
            :nudge-right="40"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
          <template v-slot:activator="{ on }">
            <v-text-field
              v-model="effectiveDate"
              label="Effective on"
              prepend-icon="mdi-calendar"
              hint="YYYY-MM-DD format"
              v-on="on"
            ></v-text-field>
            </template>
            <v-date-picker v-model="effectiveDate" @input="effectiveDateMenu = false"></v-date-picker>
          </v-menu>
        </v-col>

        <v-col cols="12" sm="6" md="4">
          <v-menu
            v-model="expiryDateMenu"
            :close-on-content-click="false"
            :nudge-right="40"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
          <template v-slot:activator="{ on }">
            <v-text-field
              v-model="expiryDate"
              label="Expired on (Optional)"
              prepend-icon="mdi-calendar"
              hint="YYYY-MM-DD format"
              v-on="on"
              clearable
            ></v-text-field>
            </template>
            <v-date-picker v-model="expiryDate" @input="expiryDateMenu = false"></v-date-picker>
          </v-menu>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6" md="4">
          <v-autocomplete
            v-model="supporters"
            :items="USERS"
            item-text="name"
            item-value="name"
            chips
            clearable
            label="Supported by"
            prepend-icon="mdi-account"
            multiple deletable-chips
          ></v-autocomplete>
        </v-col>
        <v-col cols="6" md="4">
          <v-autocomplete
            v-model="approvers"
            :items="APPROVERS"
            item-text="name"
            item-value="name"
            chips
            clearable
            label="Approved by (Optional)"
            hint="Used to replace default approvers"
            prepend-icon="mdi-account"
            multiple deletable-chips
          ></v-autocomplete>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6" md="4">
          <v-file-input multiple chips show-size counter v-model="attachments"
            label="Supporting documents (Optional)"></v-file-input>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="12">
          <v-textarea
            v-model="comments"
            label="Comments (Optional)"
            clearable
            rows="3"
          ></v-textarea>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="12">
          <v-textarea
            v-model="manual"
            label="Manual Access Changes (Optional)"
            hint="When you need assistance or when required role(s) is not listed"
            clearable
            rows="3"
          ></v-textarea>
        </v-col>
      </v-row>
      
      <div class="padtop"><h3>Please specify the access of each user</h3></div>
      <v-divider></v-divider>
      
      <v-row v-for="user of selectedUsers" v-bind:key="user.name">
        <v-col cols="12" md="12">
          <UserDetails class="paduser" v-bind:user="user" />
        </v-col>
      </v-row>
    </v-container>
  </v-form>
</template>

<script>

import { mapGetters } from 'vuex';
import { mapFields } from 'vuex-map-fields';
import UserDetails from '../shared/UserDetails.vue';

export default {
  components: {
    UserDetails,
  },
  mounted() {
      if(this.ROLES.length===0)
        this.$store.dispatch('GET_ROLES');
      if(this.APPROVERS.length===0)
        this.$store.dispatch('GET_APPROVERS');
    },
  data: () => ({
    effectiveDateMenu: false,
    expiryDateMenu: false,
    valid: false,
  }),
  computed: {
    selectedUsers() {
      return this.USERS.filter(user => user.selected);
    },
    ...mapGetters(['USERS', 'ROLES', 'APPROVERS']),
    ...mapFields([
      'requestForm.effectiveDate',
      'requestForm.expiryDate',
      'requestForm.comments',
      'requestForm.manual',
      'requestForm.purpose',
      'requestForm.supporters',
      'requestForm.approvers',
      'requestForm.attachments',
    ]),
  },
};
</script>

<style scoped>
  .padtop {
    padding-top: 100px;
  }
  .paduser {
    padding: 15px 10px 10px 10px;
  }
</style>
