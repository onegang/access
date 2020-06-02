<template>
  <v-container>
    <v-row>
      <v-col cols="3">
        <h4>{{request.status}}</h4>
      </v-col>
      <v-col cols="9">
        <v-btn v-show="has('Cancel')" small color="error" class="float-right mx-1">
          <v-icon>mdi-close</v-icon>Cancel Request
        </v-btn>
        <v-btn v-show="has('Reject')" small color="error" class="float-right mx-1">
          <v-icon>mdi-close</v-icon>Reject
        </v-btn>
        <v-btn v-show="has('Approve')" small color="primary" class="float-right mx-1">
          <v-icon>mdi-check</v-icon>Approve
        </v-btn>        
      </v-col>
    </v-row>
    <div class="py-1">
      <span class="subtitle-2">Requested by: </span>{{request.requestor}}
    </div>
    <div v-if="request.purpose" class="py-1">
      <span class="subtitle-2">Purpose: </span>{{request.purpose}}
    </div>
    <div class="py-1">
      <span class="subtitle-2">Effective Date: </span>{{moment(request.effectiveDate).format('YYYY-MM-DD')}}
    </div>
    <div v-if="request.expiryDate" class="py-1">
      <span class="subtitle-2">Expiry Date: </span>{{request.expiryDate}}
    </div>
    <div v-if="request.comments" class="py-1">
      <span class="subtitle-2">Comments: </span>{{request.comments}}
    </div>
    <!-- <div v-if="request.attachments.length>0">Supporting Documents: {{request.attachments.map(file => file.name).join(", ")}}</div> -->
    <div class="inline py-1">
      <span class="subtitle-2">Supporters: </span>
      <v-chip v-for="(user, index) in request.supporters" :key="`s-${index}`"
        class="mx-1" label small
        :outlined="isPending(user)"
        :color="getColor(user)">
        <v-avatar left><v-icon>mdi-account</v-icon></v-avatar>
        {{user.name}}: {{user.status}}
      </v-chip>
    </div>

    <div class="inline py-1">
      <span class="subtitle-2">Approvers: </span>
      <v-chip v-for="(user, index) in request.approvers" :key="`s-${index}`"
        class="mx-1" label small
        :outlined="isPending(user)"
        :color="getColor(user)">
        <v-avatar left><v-icon>mdi-account</v-icon></v-avatar>
        {{user.name}}: {{user.status}}
      </v-chip>
    </div>
    

    <h3 class="pt-10">Access Changes</h3>
    <v-divider class="pb-3" />
    <div v-if="request.manual" class="py-1">
      {{request.manual}}
    </div>
    
    <ChangeItem class="py-1" v-for="(added, index) in request.changes.added" :key="`add-${index}`" :change="added" type="ADD" />
  
    <ChangeItem class="py-1" v-for="(removed, index) in request.changes.removed" :key="`remove-${index}`" :change="removed" type="REMOVE" />
    
    <h3 class="pt-10">Effective User Access</h3>
    <v-divider class="pb-3" />
    <div v-for="user of request.users" v-bind:key="user.name">
        <UserDetails :user="user" readonly />
    </div>
  </v-container>
</template>

<script>
import UserDetails from '../home/UserDetails.vue';
import ChangeItem from '../home/ChangeItem.vue';

export default {
  components: {
    UserDetails,
    ChangeItem,
  },
  props: {
    request: {
      type: Object,
      required: true,
    },
    actions: {
      type: Array,
      required: true,
    }
  },
  methods:{
    isPending(user) {
        return user.status==="PENDING";
      },
      getColor(user) {
        if(user.status==="REJECTED")
          return "deep-orange darken-3 white--text";
        else if(user.status==="APPROVED")
          return "info"
        else
          return "";
      },
      has(action) {
        return this.actions.includes(action);
      }
  }
};
</script>

<style>
  
</style>
